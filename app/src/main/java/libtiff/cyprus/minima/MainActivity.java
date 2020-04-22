package libtiff.cyprus.minima;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
{
    public String num="8998";
    String id=null,postal=null,other=null;
    int code;//categoryid

    private static final int REQUEST_SEND_SMS= 1;

    EditText mID , mPostal , mOtherReason;
    Button mSend;
    Spinner mSpinner;
    TextView mTxtOther;

    private static String[] PERMISSIONS_APP= { Manifest.permission.SEND_SMS};

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSend= (Button)findViewById(R.id.btnsms);
        mID  = (EditText)findViewById(R.id.txtboxid);
        mPostal= (EditText)findViewById(R.id.txtboxpostal);
        mOtherReason= (EditText)findViewById(R.id.txtboxotherreason);
        mSpinner = (Spinner)findViewById(R.id.spinner1);
        mTxtOther= (TextView)findViewById(R.id.txtviewother);
        verifyPermissions(MainActivity.this);
        mSpinner.setSelection(0);

        mSend.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                id=mID.getText().toString().trim();//getid
                if (id.equals("") || id.equals(null))
                {
                    mID.requestFocus();
                    Toast.makeText(MainActivity.this, "Συμπληρώστε τον αριθμό της ταυτότητας σας. \n\nPlease enter your ID number.", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    postal=mPostal.getText().toString().trim();//getpostal
                    if (postal.equals("") || postal.equals(null))
                    {
                        mPostal.requestFocus();
                        Toast.makeText(MainActivity.this, "Συμπληρώστε τον ταχυδρομικό κώδικα της οικίας σας. \n\nPlease enter your home postal code.", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        code = mSpinner.getSelectedItemPosition();//getcategoryselection
                        if(code==0)
                        {
                            mSpinner.requestFocus();
                            Toast.makeText(MainActivity.this, "Επιλέξτε κατηγορία. \n\nPlease choose a category.", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            other=mOtherReason.getText().toString().trim();
                            if(code==8 && (other.equals(null) || other.equals("")))
                            {
                                mOtherReason.requestFocus();
                                Toast.makeText(MainActivity.this, "Παρακαλώ γράψτε τον λόγο της μετακίνησης σας. \n\nPlease write the reason you are requesting permission for leaving your house.", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                //We are done with the validation checks , proceed with sending the sms.
                                sendsms(code, other, id, postal);
                            }
                        }
                    }
                }

            }
        });

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {

                // Get the spinner selected item text
                String selectedItemText = adapterView.getItemAtPosition(i).toString();

                switch(selectedItemText)
                {
                    case "[1] Φαρμακεία-Ιατροί/Pharmacies-Doctors":
                    {
                        mTxtOther.setVisibility(View.INVISIBLE);
                        mOtherReason.setVisibility(View.INVISIBLE);
                        checkitem("Μετάβαση σε φαρμακείο ή για αιμοδοσία ή επίσκεψη σε γιατρό. \n\nVisiting a pharmacy, a doctor, or for a blood donation.");
                        break;
                    }
                    case "[2] Τρόφιμα/SuperMarkets":
                    {
                        mTxtOther.setVisibility(View.INVISIBLE);
                        mOtherReason.setVisibility(View.INVISIBLE);
                        checkitem("Μετάβαση σε κατάστημα για αγορά ή προμήθεια αγαθών/υπηρεσιών πρώτης ανάγκης. \n\nVisiting a store to obtain essential goods or services.");
                        break;
                    }
                    case "[3] Τράπεζες/Banks":
                    {
                        mTxtOther.setVisibility(View.INVISIBLE);
                        mOtherReason.setVisibility(View.INVISIBLE);
                        checkitem("Μετάβαση σε τράπεζα, στο μέτρο που δεν είναι δυνατή η ηλεκτρονική συναλλαγή. \n\nVisiting a bank if a transaction cannot be done online.");
                        break;
                    }
                    case "[4] Κρατικές Υπηρεσίες/Govermental Services":
                    {
                        mTxtOther.setVisibility(View.INVISIBLE);
                        mOtherReason.setVisibility(View.INVISIBLE);
                        checkitem("Απόλυτα αναγκαίες επισκέψεις σε κρατικές Υπηρεσίες ή Υπηρεσίες του ευρύτερου δημόσιου τομέα και της Τοπικής Αυτοδιοίκησης. \n\nAbsolutely necessary visits to state services, public-sector services or municipal services.");
                        break;
                    }
                    case "[5] Βοήθεια σε άτομα/Help People in Need":
                    {
                        mTxtOther.setVisibility(View.INVISIBLE);
                        mOtherReason.setVisibility(View.INVISIBLE);
                        checkitem("Διακίνηση για παροχή βοήθειας σε άτομα που αδυνατούν να αυτοεξυπηρετηθούν ή που οφείλουν να αυτοπροστατευθούν ή βρίσκονται σε αυτοπεριορισμό ή/και σε χώρους υποχρεωτικού περιορισμού (καραντίνα). \n\nVisiting people who are unable to help themselves or are in self-isolation.");
                        break;
                    }
                    case "[6] Άσκηση-Κατοικίδια/Exercising-Pets":
                    {
                        mTxtOther.setVisibility(View.INVISIBLE);
                        mOtherReason.setVisibility(View.INVISIBLE);
                        checkitem("Μετακίνηση για σωματική άσκηση ή για τις ανάγκες κατοικίδιου ζώου, εφόσον δεν υπερβαίνουν τα δύο άτομα και περιορίζονται σε γειτνιάζουσες με την κατοικία τους περιοχές. \n\nGoing outdoors for exercise or to walk one’s pet, for two persons at a maximum and the distance must be close to one’s residence.");
                        break;
                    }
                    case "[7] Τελετές/Ceremonies":
                    {
                        mTxtOther.setVisibility(View.INVISIBLE);
                        mOtherReason.setVisibility(View.INVISIBLE);
                        checkitem("Μετάβαση σε τελετή (π.χ. κηδεία, γάμος, βάφτιση) από συγγενείς πρώτου και δεύτερου βαθμού που δεν υπερβαίνουν τον αριθμό των 10 προσώπων. \n\nAttending ceremonies like funerals, weddings or baptisms, provided you are a first-degree or second-degree relative and the gathering must be no more than 10 people at any one time.");
                        break;
                    }
                    case "[8] Άλλος Λόγος/Other Reason":
                    {
                        mTxtOther.setVisibility(View.VISIBLE);
                        mOtherReason.setVisibility(View.VISIBLE);
                        checkitem("Δήλωση οποιουδήποτε άλλου σκοπού μετακίνησης που μπορεί να δικαιολογηθεί με βάση τα μέτρα απαγόρευσης της κυκλοφορίας στο πιο κάτω πεδίο. \n\nAny other reason (generic) for moving outside that may be justified despite the restrictions must be specified on the below field.");
                    }
                    default:break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(MainActivity.this,"Διαλέξτε Κατηγορία! / Select a Category!",Toast.LENGTH_LONG).show();
            }

        });

    }

    public static void verifyPermissions(Activity activity)
    {
        int smspermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.SEND_SMS);

        if (smspermission != PackageManager.PERMISSION_GRANTED)
        {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity,PERMISSIONS_APP,REQUEST_SEND_SMS);
        }

    }

    public void checkitem(String info)
    {
//create alertDialogBuilder
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        // set title
        alertDialogBuilder.setTitle("Λεπτομέρειες / Details");
        // set dialog message
        alertDialogBuilder
                .setMessage(info)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        // if this button is clicked, do something here i am displaying Toast Message
                    }
                });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

    public void sendsms(int category,String other,String id,String postal)
    {
        //SmsManager smgr = SmsManager.getDefault();
        String msgbody=null;
        if (other.trim().equals("") || other.trim().equals(null))
        {
            msgbody=category + " " + id + " " + postal.trim();

        }
        else
        {
            msgbody=category+ " " + other + " " + id + " " + postal.trim();
        }

        Uri uri = Uri.parse("smsto:" + num);
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.setData(uri);
        intent.putExtra("address", num);
        intent.putExtra("sms_body", msgbody);
        intent.putExtra("exit_on_sent", true);
        if (intent.resolveActivity(getPackageManager()) != null)
        {
            startActivityForResult(intent, 1);
            Toast.makeText(this, "Το μήνυμα δημιουρήθηκε με επιτυχία, πατήστε αποστολή για να το στείλετε. \n\n Υour message has been constructed successfully,you can now press the send button.", '9').show();
            mOtherReason.setText(null);
            mID.setText(null);
            mPostal.setText(null);
            mSpinner.setSelection(0);
        }
        else
        {
            Toast.makeText(this, "No SMS provider found.", Toast.LENGTH_SHORT).show();
        }

        //smgr.sendTextMessage(num,null,msgbody,null,null);
        //Log.d("MSG VALUE", msgbody);
    }

}


