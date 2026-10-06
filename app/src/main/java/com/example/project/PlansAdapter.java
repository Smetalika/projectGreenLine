package com.example.project;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PlansAdapter extends RecyclerView.Adapter<PlansAdapter.ViewHolder> {

    private List<PlansActivity.PlanItem> plans;
    private OnDeleteClickListener deleteListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }

    public PlansAdapter(List<PlansActivity.PlanItem> plans, OnDeleteClickListener deleteListener) {
        this.plans = plans;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_plan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PlansActivity.PlanItem plan = plans.get(position);
        holder.textName.setText(plan.getName());
        holder.textDateTime.setText(plan.getDateTime());

        holder.btnDelete.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDeleteClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return plans.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textName;
        TextView textDateTime;
        ImageButton btnDelete;

        ViewHolder(View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textPlanName);
            textDateTime = itemView.findViewById(R.id.textPlanDateTime);
            btnDelete = itemView.findViewById(R.id.btnDeletePlan);
        }
    }
}
