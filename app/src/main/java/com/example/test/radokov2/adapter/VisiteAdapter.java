package com.example.test.radokov2.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.radokov2.R;
import com.example.test.radokov2.model.Visite;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class VisiteAdapter extends FirestoreRecyclerAdapter<Visite, VisiteAdapter.VisiteHolder> {

    public PatientAdapter.OnItemClickListener listener;

    public VisiteAdapter(@NonNull FirestoreRecyclerOptions options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull VisiteHolder holder, int position, @NonNull Visite model) {
        holder.txtMaladie.setText(model.getMaladie());
        holder.txtDateDeVisite.setText(model.getDate());
        holder.txtFaitPar.setText(model.getDocteur().split(" ")[0]);
    }

    @NonNull
    @Override
    public VisiteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.visite_item,parent,false);
        return new VisiteHolder(v);
    }


    class VisiteHolder extends RecyclerView.ViewHolder{
        TextView txtMaladie,txtFaitPar,txtDateDeVisite;

        public VisiteHolder(@NonNull View itemView) {
            super(itemView);
            txtMaladie = itemView.findViewById(R.id.visite_item_maladie);
            txtDateDeVisite = itemView.findViewById(R.id.date_du_visite);
            txtFaitPar = itemView.findViewById(R.id.fait_par);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position!= RecyclerView.NO_POSITION && listener != null){
                    listener.onItemClick(getSnapshots().getSnapshot(position), position);
                }
            });
        }
    }
    public interface OnItemClickListener{
        void onItemClick(int position);
    }
    public void setOnItemClickListener(PatientAdapter.OnItemClickListener listener){
        this.listener = listener;
    }
}
