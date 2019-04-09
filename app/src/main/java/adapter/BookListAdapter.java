package adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.searchify.BookObj;
import com.example.searchify.R;
import com.example.searchify.UserObj;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Objects;

public class BookListAdapter extends BaseAdapter {
    public List<BookObj> bookObjList;
    Context context;
    private FirebaseAuth mAuth;

    public BookListAdapter(List<BookObj> bookObjList, Context

            context) {
        this.context = context;
        this.bookObjList = bookObjList;

    }

    @Override
    public int getCount() {
        return bookObjList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = null;
        final BookObj book = bookObjList.get(position);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.book_list_adapter_layout, parent,

                    false);
        } else {
            v = convertView;
        }

        Typeface mTfRegular = Typeface.createFromAsset(v.getContext().getAssets(),"OpenSans-Regular.ttf");

        ImageView bookImageView = v.findViewById(R.id.book_img);
        TextView bookNameText = v.findViewById(R.id.book_name);
        TextView authorNameText = v.findViewById(R.id.author_name);
        Button reqButton = v.findViewById(R.id.req_book_btn);
        Button readButton = v.findViewById(R.id.read_book_btn);

        if(book.getAvailability().equals("no"))
        {
            reqButton.setClickable(false);
            reqButton.setText("Not Available");
        }

        reqButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send a request to book owner
                mAuth = FirebaseAuth.getInstance();
                final String user_id = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

                DatabaseReference req_ref = FirebaseDatabase.getInstance().getReference().child("Users").child("Owners").
                        child("UID").child(user_id).child("sentrequest").child(book.getName());
                req_ref.child("name").setValue(book.getName());
                req_ref.child("category").setValue(book.getCategory());
                req_ref.child("writer").setValue(book.getWriter());
                req_ref.child("avaiability").setValue(book.getAvailability());

                DatabaseReference uid_ref = FirebaseDatabase.getInstance().getReference().child("Users").child("Owners").
                        child("UID").child(user_id).child("username");

                uid_ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final String user_name = (String) dataSnapshot.getValue();
                        assert user_name != null;
                        final DatabaseReference user_name_ref = FirebaseDatabase.getInstance().getReference().child("Users").child("Owners").
                                child("username").child(user_name).child("sentrequest").child(book.getName());

                        user_name_ref.child("name").setValue(book.getName());
                        user_name_ref.child("category").setValue(book.getCategory());
                        user_name_ref.child("writer").setValue(book.getWriter());
                        user_name_ref.child("avaiability").setValue(book.getAvailability());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        readButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //current user e book read list e thakle unread banate hobe else read
            }
        });

        //Book Image
        bookImageView.setImageResource(R.drawable.logoo );
        bookImageView.setVisibility(View.VISIBLE);

        //Book name
        bookNameText.setTypeface(mTfRegular);
        bookNameText.setText(book.getName());
        bookNameText.setVisibility(View.VISIBLE);

        //author name
        authorNameText.setTypeface(mTfRegular);
        authorNameText.setText(book.getWriter());
        authorNameText.setVisibility(View.VISIBLE);


        return v;
    }
}