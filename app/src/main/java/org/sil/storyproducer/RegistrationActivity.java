package org.sil.storyproducer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class RegistrationActivity extends AppCompatActivity {

    private Resources classResources;
    private List<TextInputEditText> listOfTextFields;
    private final int [] viewIntId = {R.id.general_section, R.id.translator_section,R.id.consultant_section,R.id.trainer_section,R.id.database_section};
    private final int [] headIntId = {R.id.general_header, R.id.translator_header, R.id.consultant_header, R.id.trainer_header, R.id.database_header};
    private View[] mySelectionViews = new View[viewIntId.length];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        for(int i = 0; i < viewIntId.length; i++){
            mySelectionViews[i] = findViewById(viewIntId[i]);
            setAccordionListener(findViewById(headIntId[i]), mySelectionViews[i]);
        }

        //Used later in a context that needs the resources
        classResources = this.getResources();
    }

    protected void onPostCreate(Bundle savedInstanceState){
        super.onPostCreate(savedInstanceState);
        this.setupTextFields();
        this.addSubmitButtonSave();
    }

    /***
     * Initializes the listOfTextFields to the text fields in the activity.
     */
    private void setupTextFields(){
        View view = findViewById(R.id.scroll_view);

        //Find the top level linear layout
        if(view instanceof ScrollView) {
            ScrollView scrollView = (ScrollView) view;
            listOfTextFields = getTextFields(scrollView);
        }
    }

    /**
     * This function adds the on click listener for the submit button.
     */
    private void addSubmitButtonSave(){
        final Button submitButton = (Button)findViewById(R.id.submit_button);
        submitButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                submitButton.requestFocus();
                if(textFieldsParsed()){
                    storeRegistrationInfo();
                    createToast(getApplicationContext(), getString(R.string.saved_successfully));
                    retrieveRegistrationInfo();
                    hideKeyboard();
                }
            }
        });
    }

    /**
     * This function takes a scroll view as the root view of a xml layout and searches for
     * TextInputEditText fields to add to the List.
     * Don't mind the superfluous casts. The multiple casts are in place so that all nodes are
     * visited, regardless of what class the node is.
     * @param rootScrollView The root scroll view where all the children will be visited to
     *                       check if there is an TextInputEditText field.
     * @return               The list of TextInputEditText fields that will be parsed.
     */
    private List<TextInputEditText> getTextFields(ScrollView rootScrollView){
        //error check
        if(rootScrollView == null){
            return null;
        }

        List<TextInputEditText> listOfEditText = new ArrayList<>();
        Stack<ViewGroup> myStack = new Stack<>();
        myStack.push(rootScrollView);

        while(myStack.size() > 0){
            ViewGroup currentView = myStack.pop();
            if(currentView instanceof TextInputLayout){
                listOfEditText.add((TextInputEditText)((TextInputLayout) currentView).getEditText());
            }
            else{
                if(currentView.getChildCount() > 0){
                    //push children onto stack from right to left
                    //pushing on in reverse order so that the traversal is in-order traversal
                    for(int i = currentView.getChildCount() - 1; i >= 0; i--){
                        View child = currentView.getChildAt(i);
                        if(child instanceof ViewGroup){
                            myStack.push((ViewGroup)child);
                        }
                    }
                }
            }
        }

        return listOfEditText;
    }

    /**
     * Parse the text fields when the submit button has been clicked.
     * @return Returns true if all the text fields are inputted correctly, else,
     * returns false if text fields are not inputted correctly.
     */
    private boolean textFieldsParsed(){
        for(int i = 0; i < listOfTextFields.size(); i++){
            TextInputEditText textField = listOfTextFields.get(i);
            int type = textField.getInputType();
            String inputString = textField.getText().toString();
            ParseText.parseText(type, inputString, classResources);

            if(ParseText.hasError()){
                createErrorDialog(textField);
                textField.requestFocus();
                for(int j = 0; j < this.mySelectionViews.length; j++){
                    if(mySelectionViews[j].findFocus() != null){
                        mySelectionViews[j].setVisibility(View.VISIBLE);
                    }
                }
                return false;
            }
        }

        return true;
    }

    /**
     * Custom dialog box creation for a parsing error.
     * @param myText The TextInputEditText field that might need to regain focus depending
     *               on user input.
     * @return The dialog box that the user must encounter.
     */
    private Dialog createErrorDialog(final TextInputEditText myText){
        AlertDialog dialog = new AlertDialog.Builder(RegistrationActivity.this)
        .setTitle(" ")
        .setMessage(" ")
        .setIcon(android.R.drawable.ic_dialog_info)
        .setPositiveButton(" ", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                myText.requestFocus();
            }
        })
        .setNegativeButton(" ", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                myText.setText("");
            }
        }).create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button positiveButton = ((AlertDialog) dialog)
                        .getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setBackgroundResource(android.R.drawable.ic_menu_revert);

                Button negativeButton = ((AlertDialog) dialog)
                        .getButton(AlertDialog.BUTTON_NEGATIVE);
                negativeButton.setBackgroundResource(android.R.drawable.ic_delete);
            }
        });
        dialog.show();
        return dialog;
    }

    /***
     * Create a toast with the default location with a message.
     * @param context The current app context.
     * @param message The message that the toast will display.
     */
    private void createToast(Context context, String message){
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, message, duration);
        toast.show();
    }

    /***
     * This function retrieves the registration information from the saved preference file. The
     * preference file is located in getString(R.string.Registration_File_Name).
     */
    private void retrieveRegistrationInfo(){

        SharedPreferences prefs = getSharedPreferences(getString(R.string.Registration_File_Name), MODE_PRIVATE);
        HashMap<String, String> myMap = (HashMap<String, String>)prefs.getAll();

        System.out.println("                ");
        System.out.println("                ");
        System.out.println("                ");
        System.out.println("                ");

        for (Map.Entry<String, String> entry : myMap.entrySet())
        {
            System.out.println(entry.getKey() + "/" + entry.getValue());
        }
    }

    /***
     * This function stores the registration information to the saved preference file. The
     * preference file is located in getString(R.string.Registration_File_Name).
     */
    private void storeRegistrationInfo(){
        SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.Registration_File_Name), MODE_PRIVATE).edit();
        for(int i = 0; i < listOfTextFields.size(); i++){
            final TextInputEditText textField = listOfTextFields.get(i);
            String textFieldName = getResources().getResourceEntryName(textField.getId());
            System.out.println(textFieldName);
            editor.putString(textFieldName, textField.getText().toString());
        }
        editor.commit();
    }

    private void hideKeyboard(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * This function sets the click listeners to implement the accordion functionality
     * for each section of the registration page
     * @param headerView a variable of type View denoting the field the user will click to open up
     *                   a section of the registration
     * @param sectionView a variable of type View denoting the section that will open up
     */
    private void setAccordionListener(View headerView, final View sectionView) {
        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sectionView.getVisibility() == View.GONE) {
                    sectionView.setVisibility(View.VISIBLE);
                } else {
                    sectionView.setVisibility(View.GONE);
                }
            }
        });
    }

}
