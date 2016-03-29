package com.example.wei.possessionmanager.view;


import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.wei.possessionmanager.R;
import com.example.wei.possessionmanager.presenter.DetailPresenter;
import com.example.wei.possessionmanager.utils.Utils;
import com.example.wei.possessionmanager.bean.Item;
import com.example.wei.possessionmanager.model.ItemManager;

import java.io.File;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailFragment extends Fragment implements IDetailView {

    private static final String TAG = "DetailFragment";
    private static final String ARG_UUID = "param1";

    private static final int REQUEST_CONTACT = 0;
    private static final int REQUEST_DATE = 1;
    private static final int REQUEST_CAMERA = 2;

    @InjectView(R.id.name)
    EditText mNameEdit;

    @InjectView(R.id.owner)
    Button mOwnerButton;

    @InjectView(R.id.date)
    Button mDateButton;

    @InjectView(R.id.picture)
    ImageView mImageView;

    private DetailPresenter mDetailPresenter;
    private String mUuid;

    public DetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param uuid Parameter 1.
     * @return A new instance of fragment DetailFragment.
     */
    public static DetailFragment newInstance(String uuid) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_UUID, uuid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Bundle args = getArguments();
        mUuid = args.getString(ARG_UUID);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.inject(this, view);

        mDetailPresenter = new DetailPresenter(this, ItemManager.getInstance(getActivity()));
        mDetailPresenter.loadItem(mUuid);

        mNameEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mDetailPresenter.saveItem();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        final Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        mOwnerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(intent, REQUEST_CONTACT);
            }
        });
        PackageManager pm = getActivity().getPackageManager();
        if (pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mOwnerButton.setEnabled(false);
        }

        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DateDialogFragment fragment = DateDialogFragment.newInstance((Date) mDateButton.getTag());
                fragment.setTargetFragment(DetailFragment.this, REQUEST_DATE);
                fragment.show(getChildFragmentManager(), null);
            }
        });

        updatePhotoView();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        mDetailPresenter.saveItem();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_detail, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.camera:
                File file = getPhotoFile();
                Uri uri = Uri.fromFile(file);
                Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(captureIntent, REQUEST_CAMERA);
                break;
            case R.id.share:
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                String shareText = getString(R.string.share_string,
                        mNameEdit.getText(), mOwnerButton.getTag(), mDateButton.getText());
                String shareSubject = getString(R.string.share_subject);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, shareSubject);
                Intent chooser = Intent.createChooser(shareIntent, getString(R.string.share_title));
                startActivity(chooser);
                break;
            case R.id.delete:
                mDetailPresenter.deleteItem();
                getActivity().finish();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private File getPhotoFile() {
        return ItemManager.getInstance(getActivity()).getItemPhotoFileWithUuid(mUuid);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case REQUEST_CONTACT:
                if (data != null) {
                    Uri uri = data.getData();
                    ContentResolver cr = getContext().getContentResolver();
                    String[] projection = new String[] {ContactsContract.Contacts.DISPLAY_NAME};
                    Cursor cursor = cr.query(uri, projection, null, null, null);
                    if (cursor != null) {
                        if (cursor.getCount() != 0) {
                            cursor.moveToFirst();
                            String name = cursor.getString(0);
                            setOwner(name);
                            mDetailPresenter.saveItem();
                        }
                        cursor.close();
                    }
                }
                break;
            case REQUEST_DATE:
                if (data != null) {
                    Date date = (Date) data.getSerializableExtra(DateDialogFragment.EXTRA_DATE);
                    setDate(date);
                    mDetailPresenter.saveItem();
                }
                break;
            case REQUEST_CAMERA:
                updatePhotoView();
                galleryAddPic();
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    private void updatePhotoView() {
        setPhotoPath(getPhotoFile().getAbsolutePath());
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(getPhotoFile());
        mediaScanIntent.setData(contentUri);
        getActivity().sendBroadcast(mediaScanIntent);
    }

    @Override
    public void setName(String name) {
        mNameEdit.setText(name);
    }

    @Override
    public String getName() {
        return mNameEdit.getText().toString();
    }

    @Override
    public void setDate(Date date) {
        mDateButton.setTag(date);
        mDateButton.setText(Utils.getDateString(date));
    }

    @Override
    public Date getDate() {
        return (Date) mDateButton.getTag();
    }

    @Override
    public void setOwner(String owner) {
        if (!TextUtils.isEmpty(owner)) {
            Resources resources = getResources();
            mOwnerButton.setText(resources.getString(R.string.owner, owner));
            mOwnerButton.setTag(owner);
        }
    }

    @Override
    public String getOwner() {
        return (String) mOwnerButton.getTag();
    }

    @Override
    public void setPhotoPath(String photoPath) {
        Bitmap bitmap = Utils.getScaledBitmap(photoPath, getActivity());
        mImageView.setImageBitmap(bitmap);
    }
}
