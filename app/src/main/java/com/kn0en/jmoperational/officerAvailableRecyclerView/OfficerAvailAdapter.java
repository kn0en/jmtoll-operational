package com.kn0en.jmoperational.officerAvailableRecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kn0en.jmoperational.R;

import java.util.List;

public class OfficerAvailAdapter extends RecyclerView.Adapter<OfficerAvailViewHolders> {

    private List<OfficerAvailObject> itemList;
    private Context context;

    public OfficerAvailAdapter(List<OfficerAvailObject> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;
    }

    @NonNull
    @Override
    public OfficerAvailViewHolders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_officer_avail,null,false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        OfficerAvailViewHolders rcv = new OfficerAvailViewHolders(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull OfficerAvailViewHolders holder, int position) {
        holder.officerAvailableId.setText(itemList.get(position).getOfficerAvailableId());
        holder.officerName.setText(itemList.get(position).getOfficerName());
        holder.officerRuas.setText(itemList.get(position).getOfficerRuas());
        holder.officerDistance.setText(itemList.get(position).getOfficerDistance());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
