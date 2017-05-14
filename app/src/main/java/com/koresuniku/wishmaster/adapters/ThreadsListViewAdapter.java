package com.koresuniku.wishmaster.adapters;

import android.content.Intent;
import android.os.Build;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.koresuniku.wishmaster.R;
import com.koresuniku.wishmaster.activities.SingleThreadActivity;
import com.koresuniku.wishmaster.activities.ThreadsActivity;
import com.koresuniku.wishmaster.http.threads_api.models.Files;
import com.koresuniku.wishmaster.http.threads_api.models.Thread;
import com.koresuniku.wishmaster.utils.Constants;
import com.koresuniku.wishmaster.utils.StringUtils;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class ThreadsListViewAdapter extends BaseAdapter {
    private final String LOG_TAG = ThreadsListViewAdapter.class.getSimpleName();
    private ThreadsActivity mActivity;
    private LayoutInflater mLayoutInflater;
    private ViewHolder mHolder;
    private View mSingleImageItemView;
    private View mMultipleImagesItemView;
    private List<View> mViews;

    public ThreadsListViewAdapter(ThreadsActivity activity) {
        mActivity = activity;
        mLayoutInflater = activity.getLayoutInflater();
        mHolder = new ViewHolder();
        mViews = new ArrayList<>();
        inflateViews();
    }

    private void inflateViews() {
        for (Thread thread : mActivity.mSchema.getThreads()) {
            if (thread.getFiles().size() <= 1) {
                mViews.add(mLayoutInflater.inflate(R.layout.thread_item_single_image_redesign, null, false));
            }
            if (thread.getFiles().size() > 1) {
                mViews.add(mLayoutInflater.inflate(R.layout.thread_item_mltiple_images_redesign, null, false));
            }
        }
    }

    public void inflateSingleImageItemView() {
        mSingleImageItemView = mLayoutInflater.inflate(R.layout.thread_item_single_image_redesign, null, false);
    }

    public void inflateMultipleImagesItemView() {
        mMultipleImagesItemView = mLayoutInflater.inflate(R.layout.thread_item_mltiple_images_redesign, null, false);
    }

    @Override
    public int getCount() {
        return mActivity.mSchema.getThreads().size();
    }

    @Override
    public Object getItem(int position) {
        return mActivity.mSchema.getThreads().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        //TODO: implement up to 8 images functionality
        if (mActivity.mSchema.getThreads().get(position).getFiles().size() == 1) {
            return Constants.ITEM_SINGLE_IMAGE;
        }
        if (mActivity.mSchema.getThreads().get(position).getFiles().size() > 1) {
            return Constants.ITEM_MULTIPLE_IMAGES;
        }
        return Constants.ITEM_SINGLE_IMAGE;
    }

    private class ViewHolder {
        TextView numberAndTime;
        TextView subject;
        TextView comment;
        TextView postsAndFiles;
    }

    private void inflateSingleImageItemViews(View itemView) {
        mHolder.numberAndTime = (TextView) itemView.findViewById(R.id.thread_number_and_time_info);
        mHolder.subject = (TextView) itemView.findViewById(R.id.thread_subject);
        mHolder.comment = (TextView) itemView.findViewById(R.id.thread_comment);
        mHolder.postsAndFiles = (TextView) itemView.findViewById(R.id.posts_and_files_info);
    }

    private void inflateMultipleImageItemViews(View itemView) {
        mHolder.numberAndTime = (TextView) itemView.findViewById(R.id.thread_number_and_time_info);
        mHolder.subject = (TextView) itemView.findViewById(R.id.thread_subject);
        mHolder.comment = (TextView) itemView.findViewById(R.id.thread_comment);
        mHolder.postsAndFiles = (TextView) itemView.findViewById(R.id.posts_and_files_info);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //if (convertView != null) return convertView;

        if (getItemViewType(position) == Constants.ITEM_SINGLE_IMAGE) {
            //inflateSingleImageItemView();
            convertView = mViews.get(position);
            inflateSingleImageItemViews(convertView);
        }
        if (getItemViewType(position) == Constants.ITEM_MULTIPLE_IMAGES) {
            //inflateMultipleImagesItemView();
            convertView = mViews.get(position);
            inflateMultipleImageItemViews(convertView);
        }



        Thread thread = mActivity.mSchema.getThreads().get(position);
        final String number = thread.getNum();
        String time = thread.getDate();
        String name = thread.getName();
        String trip = thread.getTrip();
        String subject = thread.getSubject();
        String comment = thread.getComment();
        String postsCount = thread.getPostsCount();
        String filesCount = thread.getFilesCount();
        List<Files> files = thread.getFiles();

        mHolder.numberAndTime.setText("№" + number + (name.equals("") ? "" : " "
                + name) + " " + (trip.equals("") ? "" : " " + trip) + time);
        if (!mActivity.boardId.equals("b")) {
            mHolder.subject.setText(Html.fromHtml(subject));
        } else {
            mHolder.subject.setVisibility(View.GONE);
        }
        if (subject.equals("")) mHolder.subject.setVisibility(View.GONE);
        mHolder.comment.setText(Html.fromHtml(comment));
        mHolder.comment.setMovementMethod(LinkMovementMethod.getInstance());
        mHolder.postsAndFiles.setText(StringUtils.correctPostsAndFilesString(postsCount, filesCount));

        mHolder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToNextActivity(number);
            }
        });

        convertView.setTag(mHolder);

        return convertView;
    }

    private void goToNextActivity(String number) {
        Intent intent = new Intent(mActivity, SingleThreadActivity.class);

        intent.putExtra(Constants.BOARD_ID, mActivity.boardId);
        intent.putExtra(Constants.BOARD_NAME, mActivity.boardName);
        intent.putExtra(Constants.THREAD_NUMBER, number);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mActivity.startActivity(intent);
            mActivity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        } else mActivity.startActivity(intent);
    }
}
