package com.kn0en.jmoperational.userRequestRecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kn0en.jmoperational.UserRequestSingleActivity;
import com.kn0en.jmoperational.R;

public class UserRequestViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView riderRequestId, riderName, riderRuas;

    public UserRequestViewHolders(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        riderRequestId = (TextView) itemView.findViewById(R.id.riderRequestId);
        riderName = (TextView) itemView.findViewById(R.id.riderName);
        riderRuas = (TextView) itemView.findViewById(R.id.riderRuas);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(view.getContext(), UserRequestSingleActivity.class);
        Bundle b = new Bundle();
        b.putString("riderRequestId", riderRequestId.getText().toString());
        intent.putExtras(b);
        view.getContext().startActivity(intent);
    }
}
