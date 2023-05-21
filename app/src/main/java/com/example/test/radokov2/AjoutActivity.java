package com.example.test.radokov2;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceContour;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.google.mlkit.vision.face.FaceLandmark;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class AjoutActivity extends AppCompatActivity {

    ImageView imageView,btnFinger;
    EditText nom, prenom, dateNaissance, adresse, tel;
    RadioGroup genre;
    Button btnAjouter;
    ProgressBar progressBar;
    String stGenre = "Femme";
    Uri filepath;
    Map<String, Object> patient = new HashMap<>();
    Map<String, Object> empreintes = new HashMap<>();
    Map<String, Object> facesFirebase = new HashMap<>();
    Bitmap bitmap;



    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajout);

        mAuth = FirebaseAuth.getInstance();

        imageView = findViewById(R.id.add_photo);
        imageView.setOnClickListener(v -> {
            CropImage.activity().setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);
        });


        nom = findViewById(R.id.ajout_nom);
        prenom = findViewById(R.id.ajout_prenom);
        dateNaissance = findViewById(R.id.date_naissance);
        adresse = findViewById(R.id.addresse);
        tel = findViewById(R.id.telephone);
        genre = findViewById(R.id.radioGenre);
        genre.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.radioHomme:
                    stGenre = "Homme";
                    break;
                case R.id.radioFemme:
                    stGenre = "Femme";
                    break;
                default:
                    stGenre = "Femme";
            }
        });
        progressBar = findViewById(R.id.progress_ajout_patient);
        progressBar.setVisibility(View.INVISIBLE);

        btnAjouter = findViewById(R.id.btn_valider);
        btnAjouter.setOnClickListener(v -> {
            uploadToStorage();
        });

        btnFinger = findViewById(R.id.btn_finger);
        btnFinger.setOnClickListener(v ->{
                BiometricPrompt biometricPrompt = new BiometricPrompt(this, new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        // Get the fingerprint data
                        byte[] fingerprintData = result.getCryptoObject().getCipher().getIV();
                        empreintes.put("empreintes"+ UUID.randomUUID().toString(), Arrays.toString(fingerprintData));
                        if (empreintes.size()==3){
                            btnFinger.setImageResource(R.drawable.btn_finger_active);
                        }// Save the fingerprint data to a database or server
                    }
                });
                Cipher cipher = getCipher();
                if (cipher != null) {
                        androidx.biometric.BiometricPrompt.CryptoObject cryptoObject = new androidx.biometric.BiometricPrompt.CryptoObject(cipher);
                        // Call the authenticate() method to start the authentication process
                        biometricPrompt.authenticate(new BiometricPrompt.PromptInfo.Builder()
                                .setTitle("Entrer 5 empreintes")
                                .setDescription("Encore"+ (5))
                                .setSubtitle("Subtitle")
                                .setNegativeButtonText("Cancel")
                                .setConfirmationRequired(false)
                                .build(), cryptoObject);
                }
            }
        );

    }

    private Cipher getCipher(){
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
            KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder("key_name", KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7);
            keyGenerator.init(builder.build());
            SecretKey secretKey = keyGenerator.generateKey();
            Cipher cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return cipher;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | NoSuchProviderException | InvalidAlgorithmParameterException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void AjoutPatient(Uri uri) {
        String stNom = nom.getText().toString();
        String stPrenom = prenom.getText().toString();
        String stDateNaissance = dateNaissance.getText().toString();
        String stAdresse = adresse.getText().toString();
        String stTel = tel.getText().toString();

        if (stNom.isEmpty() || stAdresse.isEmpty() || stDateNaissance.isEmpty() || stTel.isEmpty() || stPrenom.isEmpty()) {
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(this, "Veiillez remplire tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm", Locale.FRENCH);
        String strDate = dateFormat.format(new Date());

        patient.put("nom", stNom);
        patient.put("prenom", stPrenom);
        patient.put("dateNaissance", stDateNaissance);
        patient.put("adresse", stAdresse);
        patient.put("tel", stTel);
        patient.put("genre", stGenre);
        patient.put("docteurUid", Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
        patient.put("dateAjout", strDate);
        patient.put("profilUrl", uri.toString());
        patient.put("empreintes", empreintes);
        patient.put("face", facesFirebase);

        db.collection("Patient")
                .add(patient)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(AjoutActivity.this, "Succés de l'ajout", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                    startActivity(new Intent(AjoutActivity.this, HomeActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AjoutActivity.this, "Echec de l'ajout", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.INVISIBLE);
                });

    }

    private void uploadToStorage() {
        progressBar.setVisibility(View.VISIBLE);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        if (filepath != null) {
            StorageReference fileRef = storage.getReference().child(System.currentTimeMillis() + "." + getFileExtension(filepath));
            fileRef.putFile(filepath)
                    .addOnSuccessListener(taskSnapshot -> {
                        fileRef.getDownloadUrl().addOnSuccessListener(this::AjoutPatient);
                    })
                    .addOnProgressListener(snapshot -> {

                    }).addOnFailureListener(e -> {
                Toast.makeText(AjoutActivity.this, "Error" + e, Toast.LENGTH_SHORT).show();

            });
        } else {
            Toast.makeText(AjoutActivity.this, "Veuillez ajouter un image", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileExtension(Uri filepath) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(filepath));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE || requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == Activity.RESULT_OK){
                Uri resultUri = result.getUri();
                filepath = resultUri;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),resultUri);
                    getLandmark(bitmap);
                    imageView.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
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

                                    float[] featureVector = normalize(landmarks);
                                    facesFirebase.put("faces",Arrays.toString(featureVector));

                                })
                        .addOnFailureListener(
                                e -> {
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
}