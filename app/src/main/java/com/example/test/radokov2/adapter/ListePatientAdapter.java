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

import de.hdodenhof.circleimageview.CircleImageView;

public class ListePatientAdapter extends FirestoreRecyclerAdapter<Patient, ListePatientAdapter.PatienListeHolder> {

    public PatientAdapter.OnItemClickListener listener;

    public ListePatientAdapter(@NonNull FirestoreRecyclerOptions<Patient> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull PatienListeHolder holder, int position, @NonNull Patient model) {
        holder.txtNom.setText(model.getNom());
        holder.txtPrenom.setText(model.getPrenom());
        Glide.with(holder.img.getContext())
                .load(model.getProfilUrl())
                .placeholder(R.drawable.ic_patient_home)
                .centerCrop()
                .into(holder.img);
    }

    @NonNull
    @Override
    public PatienListeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_patient_item, parent, false);
        return new PatienListeHolder(v);
    }

    class PatienListeHolder extends RecyclerView.ViewHolder {
        TextView txtNom, txtPrenom;
        CircleImageView img;

        public PatienListeHolder(@NonNull View itemView) {
            super(itemView);
            txtNom = itemView.findViewById(R.id.list_patient_nom);
            txtPrenom = itemView.findViewById(R.id.list_patient_prenom);
            img = itemView.findViewById(R.id.liste_pat_img);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(getSnapshots().getSnapshot(position), position);
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(PatientAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }
}
