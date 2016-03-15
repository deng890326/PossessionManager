package com.example.wei.possessionmanager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    @InjectView(R.id.list)
    RecyclerView mRecyclerView;

    private ItemAdapter mAdapter;

    private ThumbnailLoader<ItemHolder> mThumbnailLoader;

    public MainFragment() {
        // Required empty public constructor
    }

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mThumbnailLoader = new ThumbnailLoader<>(getActivity(), new Handler(),
                new ThumbnailLoader.OnRequestDoneListener<ItemHolder>() {
                    @Override
                    public void onRequestDone(ItemHolder target, Bitmap bitmap) {
                        if (bitmap != null) {
                            target.mThumbnail.setImageBitmap(bitmap);
                        }
                    }
                });
        mThumbnailLoader.start();
        mThumbnailLoader.getLooper();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.inject(this, view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateAdapter();
    }

    @Override
    public void onPause() {
        super.onPause();
        mThumbnailLoader.clearQueue();
    }

    private void updateAdapter() {
        List<Item> items = ItemManager.getInstance(getActivity()).getAllItems();
        if (mAdapter == null) {
            mAdapter = new ItemAdapter(items);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setItems(items);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_main, menu);
        Log.d(getTag(), "onCreateOptionsMenu");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                Item newItem = new Item();
                ItemManager.getInstance(getActivity()).addItem(newItem);

                Intent intent = DetailActivity.newIntent(getActivity(), newItem.getUUID().toString());
                startActivity(intent);
                return true;
            default:
                return onOptionsItemSelected(item);
        }
    }

    private void onItemPressed(Item item) {
        if (mListener != null) {
            mListener.onFragmentInteraction(item);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @InjectView(R.id.thumbnail)
        ImageView mThumbnail;

        @InjectView(R.id.name)
        TextView mNameText;

        @InjectView(R.id.date)
        TextView mDateText;

        private Item mItem;

        public ItemHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
            itemView.setOnClickListener(this);
        }

        public void bindItem(Item item) {
            mItem = item;
            String date = Utils.getDateString(mItem.getDate());

            mNameText.setText(item.getName());
            mDateText.setText(date);

            ItemManager im = ItemManager.getInstance(getContext());
            String path = im.getItemPhotoFile(mItem).toString();
            if (!mThumbnailLoader.isCached(path)) {
                mThumbnail.setImageResource(R.drawable.picture);
            }
            mThumbnailLoader.queueThumbnail(this, path);
        }

        @Override
        public void onClick(View view) {
            if (mItem != null) {
                onItemPressed(mItem);
            }
        }
    }

    private class ItemAdapter extends RecyclerView.Adapter<ItemHolder> {

        private List<Item> mItems;

        public ItemAdapter(List<Item> items) {
            mItems = items;
        }

        public void setItems(List<Item> items) {
            mItems = items;
        }

        @Override
        public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.list_item, parent, false);
            return new ItemHolder(view);
        }

        @Override
        public void onBindViewHolder(ItemHolder holder, int position) {
            holder.bindItem(mItems.get(position));
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Item item);
    }
}
