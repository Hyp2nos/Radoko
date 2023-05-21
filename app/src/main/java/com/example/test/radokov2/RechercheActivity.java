package com.example.test.radokov2;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.radokov2.adapter.ListePatientAdapter;
import com.example.test.radokov2.model.Patient;
import com.example.test.radokov2.utils.WrapContentLinearLayoutManager;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.google.mlkit.vision.face.FaceLandmark;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicReference;

public class RechercheActivity extends AppCompatActivity {

    ImageView search;
    ImageView face;
    ImageView qr;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference visiteRef = db.collection("Patient");
    private ListePatientAdapter adapter;
    RecyclerView listRecherche;
    EditText txtSrch;
    Bitmap bitmap;
    Map<Double,DocumentSnapshot> resultCompare = new HashMap<>();
    List<String> retourQr = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recherche);
        txtSrch = findViewById(R.id.chercher);
        listRecherche = findViewById(R.id.list_recherche);
        search = findViewById(R.id.img_srch);
        face = findViewById(R.id.img_face);
        qr = findViewById(R.id.img_qr);
        search.setOnClickListener(v -> {
            firebaseSearch(txtSrch.getText().toString());
            adapter.setOnItemClickListener((documentSnapshot, position) -> {
                Patient pat = documentSnapshot.toObject(Patient.class);
                String currentPatientId = documentSnapshot.getId();
                Intent toDetails = new Intent(RechercheActivity.this, DetailActivity.class);
                toDetails.putExtra("Patient",pat);
                toDetails.putExtra("currentPatientId",currentPatientId);
                startActivity(toDetails);
            });
        });
        face.setOnClickListener(v -> CropImage.activity().setGuidelines(CropImageView.Guidelines.ON)
                .start(this));

        qr.setOnClickListener(v -> CropImage.activity().setGuidelines(CropImageView.Guidelines.ON)
                .start(this));
    }

    private void firebaseSearch(String str) {
        Query query = visiteRef.orderBy("nom").startAt(str).endAt(str + "~~");

        FirestoreRecyclerOptions<Patient> options = new FirestoreRecyclerOptions.Builder<Patient>()
                .setQuery(query, Patient.class)
                .build();
        adapter = new ListePatientAdapter(options);
        listRecherche.setHasFixedSize(true);
        listRecherche.setLayoutManager(new WrapContentLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        listRecherche.setAdapter(adapter);
        adapter.startListening();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE || requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == Activity.RESULT_OK){
                Uri resultUri = result.getUri();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),resultUri);
                    String id = scanCodeQr(bitmap);
                    getLandmark(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String scanCodeQr(Bitmap bitmap) {
        BarcodeScannerOptions options =
                new BarcodeScannerOptions.Builder()
                        .setBarcodeFormats(
                                Barcode.FORMAT_QR_CODE,
                                Barcode.FORMAT_AZTEC)
                        .build();
        InputImage image = InputImage.fromBitmap(bitmap, 0);
        BarcodeScanner scanner = BarcodeScanning.getClient();
        Task<List<Barcode>> result = scanner.process(image)
                .addOnSuccessListener(barcodes -> {
                    for (Barcode barcode : barcodes){
                        String rawValue = barcode.getRawValue();
                        retourQr.add(rawValue);

                    }
                }).addOnCompleteListener(task1 -> {
                    String rawValue = retourQr.get(0).trim();
                    if (rawValue == null) return;
                    db.collection("Patient").document(""+rawValue+"")
                            .get()
                            .addOnSuccessListener(task -> {
                                Toast.makeText(RechercheActivity.this, "Le texte est "+rawValue, Toast.LENGTH_SHORT).show();
                                DocumentSnapshot document = task;
                                if (document.exists()){
                                    Patient pat = document.toObject(Patient.class);
                                    String currentPatientId = document.getId();
                                    Intent toDetails = new Intent(RechercheActivity.this, DetailActivity.class);
                                    toDetails.putExtra("Patient",pat);
                                    toDetails.putExtra("currentPatientId",currentPatientId);
                                    startActivity(toDetails);
                                }
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(RechercheActivity.this, "Erreur"+e, Toast.LENGTH_SHORT).show();
                });
        return null;
    }

    private void getLandmark(Bitmap bitmap){
        FaceDetectorOptions highAccuracyOpts =
                new FaceDetectorOptions.Builder()
                        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                        .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
                        .build();

        FaceDetector detector = FaceDetection.getClient(highAccuracyOpts);
        InputImage image = InputImage.fromBitmap(bitmap, 0);

        Task<List<Face>> result =
                detector.process(image)
                        .addOnSuccessListener(
                                faces -> {
                                    if (faces.isEmpty()) return;
                                    Face face = faces.get(0);
                                    FaceLandmark leftEye = face.getLandmark(FaceLandmark.LEFT_EYE);
                                    FaceLandmark rightEye = face.getLandmark(FaceLandmark.RIGHT_EYE);
                                    FaceLandmark noseBase = face.getLandmark(FaceLandmark.NOSE_BASE);
                                    FaceLandmark mouthLeft = face.getLandmark(FaceLandmark.MOUTH_LEFT);
                                    FaceLandmark mouthRight = face.getLandmark(FaceLandmark.MOUTH_RIGHT);

                                    List<PointF> landmarks = new ArrayList<>();
                                    if (leftEye !=null && rightEye !=null && noseBase !=null && mouthLeft!=null && mouthRight!=null){
                                        landmarks.add(leftEye.getPosition());
                                        landmarks.add(rightEye.getPosition());
                                        landmarks.add(noseBase.getPosition());
                                        landmarks.add(mouthLeft.getPosition());
                                        landmarks.add(mouthRight.getPosition());
                                    }

                                    float[] featureVector1 = normalize(landmarks);
                                    db.collection("Patient")
                                            .get()
                                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                                    Map<String, String> faceMap = (Map<String, String>) documentSnapshot.get("face");
                                                    if (faceMap == null) continue;
                                                    String floatArrayString = faceMap.get("faces");

                                                    if (floatArrayString != null) {

                                                        String[] stringArray = floatArrayString.replace("[","").replace("]","").split(",");
                                                        float[] floatArray = new float[stringArray.length];
                                                        for (int i = 0; i < stringArray.length; i++) {
                                                            floatArray[i] = Float.parseFloat(stringArray[i]);
                                                        }
                                                        double distance = calculateEuclideanDistance(featureVector1,floatArray);
                                                        Toast.makeText(RechercheActivity.this, "La distance est"+distance, Toast.LENGTH_SHORT).show();
                                                        if (distance < 0.2){
                                                            resultCompare.put(distance,documentSnapshot);
                                                        }
                                                    }

                                                }

                                            })
                                            .addOnCompleteListener(task -> {
                                                SortedMap<Double, DocumentSnapshot> sortedMap = new TreeMap<>(Comparator.naturalOrder());
                                                sortedMap.putAll(resultCompare);
                                                DocumentSnapshot documentSnapshot = sortedMap.get(sortedMap.firstKey());

                                                Patient pat = documentSnapshot.toObject(Patient.class);
                                                String currentPatientId = documentSnapshot.getId();
                                                Intent toDetails = new Intent(RechercheActivity.this, DetailActivity.class);
                                                toDetails.putExtra("Patient",pat);
                                                toDetails.putExtra("currentPatientId",currentPatientId);
                                                startActivity(toDetails);
                                            })
                                            .addOnFailureListener(e ->  Toast.makeText(RechercheActivity.this, "Erreur lors de la comparaison des visage", Toast.LENGTH_SHORT).show());
                                })
                        .addOnFailureListener(
                                e -> {
                                    if (e.equals(IndexOutOfBoundsException.class))
                                        Toast.makeText(RechercheActivity.this, "Aucun patient ne correspond", Toast.LENGTH_SHORT).show();
                                    Toast.makeText(this, "Erreur au niveau du detection de visage", Toast.LENGTH_LONG).show();
                                });
    }
    private float[] normalize(List<PointF> landmarks){

        float[] featureVector = new float[landmarks.size() * 2];

        for (int i = 0; i < landmarks.size(); i++) {
            featureVector[i * 2] = landmarks.get(i).x;
            featureVector[i * 2 + 1] = landmarks.get(i).y;
        }

// Normalisation du vecteur
        float meanX = 0, meanY = 0;
        for (int i = 0; i < featureVector.length; i += 2) {
            meanX += featureVector[i];
            meanY += featureVector[i + 1];
        }
        meanX /= landmarks.size();
        meanY /= landmarks.size();

        float stdX = 0, stdY = 0;
        for (int i = 0; i < featureVector.length; i += 2) {
            stdX += Math.pow(featureVector[i] - meanX, 2);
            stdY += Math.pow(featureVector[i + 1] - meanY, 2);
        }
        stdX /= landmarks.size();
        stdY /= landmarks.size();
        stdX = (float) Math.sqrt(stdX);
        stdY = (float) Math.sqrt(stdY);

        for (int i = 0; i < featureVector.length; i += 2) {
            featureVector[i] = (featureVector[i] - meanX) / stdX;
            featureVector[i + 1] = (featureVector[i + 1] - meanY) / stdY;
        }

        return featureVector;
    }
    private double calculateEuclideanDistance(float[] vector1, float[] vector2) {
        double sum = 0.0;
        for (int i = 0; i < vector1.length; i++) {
            sum += Math.pow(vector1[i] - vector2[i], 2);
        }
        return Math.sqrt(sum);
    }

}