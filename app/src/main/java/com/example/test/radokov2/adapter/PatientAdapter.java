package com.example.test.radokov2.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.test.radokov2.R;
import com.example.test.radokov2.model.Patient;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

import de.hdodenhof.circleimageview.CircleImageView;

public class PatientAdapter extends FirestoreRecyclerAdapter<Patient, PatientAdapter.PatientHolder> {

    public OnItemClickListener listener;


    public PatientAdapter(@NonNull FirestoreRecyclerOptions<Patient> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull PatientHolder holder, int position, @NonNull Patient model) {
        holder.nom.setText(model.getNom());
        holder.prenom.setText(model.getPrenom());
        Glide.with(holder.img.getContext())
                .load(model.getProfilUrl())
                .placeholder(R.drawable.ic_patient_home)
                .centerCrop()
                .into(holder.img);
    }

    @NonNull
    @Override
    public PatientHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.patient_item, parent, false);
        return new PatientHolder(v);
    }


    class PatientHolder extends RecyclerView.ViewHolder {

        TextView nom, prenom;
        CircleImageView img;

        public PatientHolder(@NonNull View itemView) {
            super(itemView);

            nom = itemView.findViewById(R.id.nom);
            prenom = itemView.findViewById(R.id.prenom);
            img = itemView.findViewById(R.id.pat_img);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(getSnapshots().getSnapshot(position), position);
                }
            });

        }
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
