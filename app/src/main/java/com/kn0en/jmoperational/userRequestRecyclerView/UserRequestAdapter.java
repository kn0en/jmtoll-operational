package com.kn0en.jmoperational.userRequestRecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kn0en.jmoperational.R;

import java.util.List;

public class UserRequestAdapter extends RecyclerView.Adapter<UserRequestViewHolders> {

    private List<UserRequestObject> itemList;
    private Context context;

    public UserRequestAdapter(List<UserRequestObject> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;
    }

    @NonNull
    @Override
    public UserRequestViewHolders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_request,null,false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        UserRequestViewHolders rcv = new UserRequestViewHolders(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull UserRequestViewHolders holder, int position) {
        holder.riderRequestId.setText(itemList.get(position).getRiderRequestId());
        holder.riderName.setText(itemList.get(position).getRiderName());
        holder.riderRuas.setText(itemList.get(position).getRiderRuas());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
