package berna.myappcompany.chronotrainingtracker;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

public class TrainingDialog extends AppCompatDialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()); //Builder for the dialog
        builder.setTitle("ATTENTION!!") //Dialog title
                .setMessage("No distance specified, please enter a value") //Dialog message
                .setPositiveButton("OK", new DialogInterface.OnClickListener() { //Dialog button
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Leave it blank to dismiss message on click
            }
        });

        return builder.create(); //Need to return a dialog, take the builder and let him create the dialog
    }
}
