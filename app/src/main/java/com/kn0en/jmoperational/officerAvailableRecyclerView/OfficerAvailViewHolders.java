package com.kn0en.jmoperational.officerAvailableRecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kn0en.jmoperational.OfficerAvailableSingleActivity;
import com.kn0en.jmoperational.R;
import com.kn0en.jmoperational.UserRequestSingleActivity;

import java.util.Objects;

public class OfficerAvailViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView officerAvailableId,officerName,officerRuas,officerDistance;

    public OfficerAvailViewHolders(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        officerAvailableId = (TextView) itemView.findViewById(R.id.officerAvailableId);
        officerName = (TextView) itemView.findViewById(R.id.officerName);
        officerRuas = (TextView) itemView.findViewById(R.id.officerRuas);
        officerDistance = (TextView) itemView.findViewById(R.id.officerDistance);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(view.getContext(), OfficerAvailableSingleActivity.class);
        Bundle b = new Bundle();
        b.putString("officerAvailableId", officerAvailableId.getText().toString());
        intent.putExtras(b);
        view.getContext().startActivity(intent);

    }

}
