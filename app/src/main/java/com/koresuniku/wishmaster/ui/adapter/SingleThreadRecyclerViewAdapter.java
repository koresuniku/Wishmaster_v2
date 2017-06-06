package com.koresuniku.wishmaster.ui.adapter;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.koresuniku.wishmaster.R;
import com.koresuniku.wishmaster.ui.activity.SingleThreadActivity;
import com.koresuniku.wishmaster.http.single_thread_api.models.Post;
import com.koresuniku.wishmaster.http.threads_api.models.Files;
import com.koresuniku.wishmaster.ui.UiUtils;
import com.koresuniku.wishmaster.ui.text.AnswersLinkMovementMethod;
import com.koresuniku.wishmaster.ui.text.CommentLinkMovementMethod;
import com.koresuniku.wishmaster.ui.widget.NoScrollTextView;
import com.koresuniku.wishmaster.util.Constants;
import com.koresuniku.wishmaster.util.DeviceUtils;
import com.koresuniku.wishmaster.util.Formats;
import com.koresuniku.wishmaster.util.HtmlUtils;
import com.koresuniku.wishmaster.util.StringUtils;
import com.koresuniku.wishmaster.ui.listener.SingleThreadViewPagerOnPageChangeListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SingleThreadRecyclerViewAdapter extends RecyclerView.Adapter<SingleThreadRecyclerViewAdapter.ViewHolder> {
    private final String LOG_TAG = SingleThreadRecyclerViewAdapter.class.getSimpleName();

    private SingleThreadRecyclerViewAdapter thisAdapter = this;
    private SingleThreadActivity mActivity;
    public Map<String, List<String>> mAnswers;
    public List<List<Integer>> mAnswersSpansLocations;
    public List<List<Integer>> mCommentAnswersSpansLocations;
    public BackgroundColorSpan backgroundColorSpan;
    public StyleSpan foregroundColorSpan;
    private String boardId;
    private int mViewType;
    private FrameLayout singleViewContainer;
    private View singleView;
    public boolean notifySingleView = false;

    public SingleThreadRecyclerViewAdapter(SingleThreadActivity activity, String board) {
        mActivity = activity;
        backgroundColorSpan = new BackgroundColorSpan(mActivity.getResources().getColor(R.color.link_background));
        //foregroundColorSpan = new ForegroundColorSpan(mActivity.getResources().getColor(R.color.link_background));
        foregroundColorSpan = new StyleSpan(android.graphics.Typeface.BOLD);
        boardId = board;
        getAnswersForPost();
        setupLocationsList();
        Log.d(LOG_TAG, "mAnswers: " + mAnswers);
    }



    @Override
    public long getItemId(int position) {
        return position;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout threadItemContainer;
        private TextView numberAndTime;
        private TextView subject;
        private RelativeLayout imageAndSummaryContainer1;
        private RelativeLayout imageAndSummaryContainer2;
        private RelativeLayout imageAndSummaryContainer3;
        private RelativeLayout imageAndSummaryContainer4;
        private RelativeLayout imageAndSummaryContainer5;
        private RelativeLayout imageAndSummaryContainer6;
        private RelativeLayout imageAndSummaryContainer7;
        private RelativeLayout imageAndSummaryContainer8;
        private ImageView image;
        private ImageView image1;
        private ImageView image2;
        private ImageView image3;
        private ImageView image4;
        private ImageView image5;
        private ImageView image6;
        private ImageView image7;
        private ImageView image8;
        private ImageView webmImageView;
        private ImageView webmImageView1;
        private ImageView webmImageView2;
        private ImageView webmImageView3;
        private ImageView webmImageView4;
        private ImageView webmImageView5;
        private ImageView webmImageView6;
        private ImageView webmImageView7;
        private ImageView webmImageView8;
        private TextView summary;
        private TextView summary1;
        private TextView summary2;
        private TextView summary3;
        private TextView summary4;
        private TextView summary5;
        private TextView summary6;
        private TextView summary7;
        private TextView summary8;
        private TextView comment;
        private NoScrollTextView answers;
        private Bitmap webmBitmap;

        public ViewHolder(View itemView) {
            super(itemView);

            threadItemContainer = (RelativeLayout) itemView.findViewById(R.id.post_item_container);
            numberAndTime = (TextView) itemView.findViewById(R.id.post_number_and_time_info);
            subject = (TextView) itemView.findViewById(R.id.post_subject);
            if (mViewType == Constants.ITEM_SINGLE_IMAGE) {
                image = (ImageView) itemView.findViewById(R.id.post_image);
                webmImageView = (ImageView) itemView.findViewById(R.id.webm_imageview);
                summary = (TextView) itemView.findViewById(R.id.image_summary);
            }
            if (mViewType == Constants.ITEM_MULTIPLE_IMAGES) {
                imageAndSummaryContainer1 =
                        (RelativeLayout) itemView.findViewById(R.id.image_with_summary_container_1);
                imageAndSummaryContainer2 =
                        (RelativeLayout) itemView.findViewById(R.id.image_with_summary_container_2);
                imageAndSummaryContainer3 =
                        (RelativeLayout) itemView.findViewById(R.id.image_with_summary_container_3);
                imageAndSummaryContainer4 =
                        (RelativeLayout) itemView.findViewById(R.id.image_with_summary_container_4);
                imageAndSummaryContainer5 =
                        (RelativeLayout) itemView.findViewById(R.id.image_with_summary_container_5);
                imageAndSummaryContainer6 =
                        (RelativeLayout) itemView.findViewById(R.id.image_with_summary_container_6);
                imageAndSummaryContainer7 =
                        (RelativeLayout) itemView.findViewById(R.id.image_with_summary_container_7);
                imageAndSummaryContainer8 =
                        (RelativeLayout) itemView.findViewById(R.id.image_with_summary_container_8);
                image1 = (ImageView) itemView.findViewById(R.id.post_image_1);
                webmImageView1 = (ImageView) itemView.findViewById(R.id.webm_imageview_1);
                image2 = (ImageView) itemView.findViewById(R.id.post_image_2);
                webmImageView2 = (ImageView) itemView.findViewById(R.id.webm_imageview_2);
                image3 = (ImageView) itemView.findViewById(R.id.post_image_3);
                webmImageView3 = (ImageView) itemView.findViewById(R.id.webm_imageview_3);
                image4 = (ImageView) itemView.findViewById(R.id.post_image_4);
                webmImageView4 = (ImageView) itemView.findViewById(R.id.webm_imageview_4);
                image5 = (ImageView) itemView.findViewById(R.id.post_image_5);
                webmImageView5 = (ImageView) itemView.findViewById(R.id.webm_imageview_5);
                image6 = (ImageView) itemView.findViewById(R.id.post_image_6);
                webmImageView6 = (ImageView) itemView.findViewById(R.id.webm_imageview_6);
                image7 = (ImageView) itemView.findViewById(R.id.post_image_7);
                webmImageView7 = (ImageView) itemView.findViewById(R.id.webm_imageview_7);
                image8 = (ImageView) itemView.findViewById(R.id.post_image_8);
                webmImageView8 = (ImageView) itemView.findViewById(R.id.webm_imageview_8);
                summary1 = (TextView) itemView.findViewById(R.id.image_summary_1);
                summary2 = (TextView) itemView.findViewById(R.id.image_summary_2);
                summary3 = (TextView) itemView.findViewById(R.id.image_summary_3);
                summary4 = (TextView) itemView.findViewById(R.id.image_summary_4);
                summary5 = (TextView) itemView.findViewById(R.id.image_summary_5);
                summary6 = (TextView) itemView.findViewById(R.id.image_summary_6);
                summary7 = (TextView) itemView.findViewById(R.id.image_summary_7);
                summary8 = (TextView) itemView.findViewById(R.id.image_summary_8);
            }
            comment = (TextView) itemView.findViewById(R.id.post_comment);
            answers = (NoScrollTextView) itemView.findViewById(R.id.answers);
            webmBitmap = BitmapFactory.decodeResource(mActivity.getResources(), R.drawable.webm);
            setIsRecyclable(true);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(LOG_TAG, "mAnswers: " + mAnswers);
        View view;
        switch (viewType) {
            case Constants.ITEM_NO_IMAGES: {
                mViewType = viewType;
                view = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.post_item_no_images_redesign, parent, false);
                return new ViewHolder(view);
            }
            case Constants.ITEM_SINGLE_IMAGE: {
                mViewType = viewType;
                view = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.post_item_single_image_redesign, parent, false);
                return new ViewHolder(view);
            }
            case Constants.ITEM_MULTIPLE_IMAGES: {
                mViewType = viewType;
                view = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.post_item_multiple_images_redesign, parent, false);
                return new ViewHolder(view);
            }
        }
        return null;
    }

    private View inflateProperView(int viewType) {
        View view;
        switch (viewType) {
            case Constants.ITEM_NO_IMAGES: {
                mViewType = viewType;
                view = LayoutInflater
                        .from(mActivity)
                        .inflate(R.layout.post_item_no_images_redesign, null, false);
                return view;
            }
            case Constants.ITEM_SINGLE_IMAGE: {
                mViewType = viewType;
                view = LayoutInflater
                        .from(mActivity)
                        .inflate(R.layout.post_item_single_image_redesign, null, false);
                return view;
            }
            case Constants.ITEM_MULTIPLE_IMAGES: {
                mViewType = viewType;
                view = LayoutInflater
                        .from(mActivity)
                        .inflate(R.layout.post_item_multiple_images_redesign, null, false);
                return view;
            }
        }
        return null;
    }

    private void getAnswersForPost() {
        mAnswers = HtmlUtils.getAnswersForPost(mActivity);
    }

    private void setupLocationsList() {
        mAnswersSpansLocations = new ArrayList<>();
        for (int i = 0; i < getItemCount(); i++) {
            mAnswersSpansLocations.add(i, new ArrayList<Integer>());
        }
        mCommentAnswersSpansLocations = new ArrayList<>();
        for (int i = 0; i < getItemCount(); i++) {
            mCommentAnswersSpansLocations.add(i, new ArrayList<Integer>());
        }
    }

    private SpannableString setAnswersBackgroundSpansIfNeeded(
            SpannableString spannableAnswersString, int position) {
//        List<Integer> spansAnswers = mAnswersSpansLocations.getByteInputStreamFromUrl(position);
//        Log.d(LOG_TAG, "spansLocations: " + spansAnswers);
//        if (spansAnswers.size() != 0) {
//            spannableAnswersString.setSpan(foregroundColorSpan,
//                    spansAnswers.getByteInputStreamFromUrl(0), spansAnswers.getByteInputStreamFromUrl(1),
//                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        }
        return spannableAnswersString;
    }

    private SpannableString setCommentAnswersBackgroundSpansIfNeeded(
            SpannableString spannableAnswersString, int position) {
//        List<Integer> spansCommentLocations = mCommentAnswersSpansLocations.getByteInputStreamFromUrl(position);
//        if (spansCommentLocations.size() != 0) {
//            spannableAnswersString.setSpan(foregroundColorSpan,
//                    spansCommentLocations.getByteInputStreamFromUrl(0), spansCommentLocations.getByteInputStreamFromUrl(1),
//                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        }
        return spannableAnswersString;
    }

    public void onAdapterChanges() {
        for (int i = mAnswersSpansLocations.size() - 1; i < thisAdapter.getItemCount(); i++) {
            mAnswersSpansLocations.add(new ArrayList<Integer>());
            mCommentAnswersSpansLocations.add(new ArrayList<Integer>());
            getAnswersForPost();
        }
    }

    private Spannable getCorrectStringForCommentTextView(String comment, int position) {
        SpannableString spannableCommentAnswerString = new SpannableString(Html.fromHtml(comment));
        return setCommentAnswersBackgroundSpansIfNeeded(spannableCommentAnswerString, position);
    }

    private void setCorrectStringForAnswersTextView(final ViewHolder holder, String number, int position) {
        if (mActivity.mAnswersManager.getAnswersMode() == 0) {
            SpannableString answersString = new SpannableString(
                    StringUtils.INSTANCE.getAnswersString(getAnswersCountForPost(number)));
            if (answersString.equals(new SpannableString("")))
                ((FrameLayout)holder.answers.getParent()).setVisibility(View.GONE);
            else ((FrameLayout)holder.answers.getParent()).setVisibility(View.VISIBLE);
            holder.answers.setTextAppearance(mActivity, android.R.style.TextAppearance_DeviceDefault);
            holder.answers.setTextColor(mActivity.getResources().getColor(R.color.colorAccent));
            answersString.setSpan(new ForegroundColorSpan(
                    mActivity.getResources().getColor(R.color.colorAccent)),
                    0, answersString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.answers.setText(answersString);
        } else {
            if (mAnswers.containsKey(number)) {
                holder.answers.setVisibility(View.VISIBLE);
                SpannableStringBuilder answerStringBuilder
                        = new SpannableStringBuilder(mActivity.getString(R.string.answers_text));
                answerStringBuilder.setSpan(new StyleSpan(android.graphics.Typeface.ITALIC),
                        0, answerStringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                answerStringBuilder.append("  ");
                for (String answer : mAnswers.get(number)) {
                    answerStringBuilder.append(">>");
                    answerStringBuilder.append(answer);
                    answerStringBuilder.setSpan(
                            new ForegroundColorSpan(mActivity.getResources().getColor(R.color.colorAccent)),
                            answerStringBuilder.length() - answer.length() - 2, answerStringBuilder.length(),
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    answerStringBuilder.setSpan(new UnderlineSpan(),
                            answerStringBuilder.length() - answer.length() - 2, answerStringBuilder.length(),
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    answerStringBuilder.append(",  ");
                }

                SpannableString spannableAnswersString = SpannableString.valueOf(
                        answerStringBuilder.delete(answerStringBuilder.length() - 3, answerStringBuilder.length()));
                spannableAnswersString = setAnswersBackgroundSpansIfNeeded(spannableAnswersString, position);
                holder.answers.setText(spannableAnswersString);

            } else holder.answers.setVisibility(View.GONE);
        }
    }

    private void setCorrectStringForAnswersTextView(final TextView answers, String number, int position) {
        if (mActivity.mAnswersManager.getAnswersMode() == 0) {
            SpannableString answersString = new SpannableString(
                    StringUtils.INSTANCE.getAnswersString(getAnswersCountForPost(number)));
            if (answersString.equals(new SpannableString("")))
                ((FrameLayout)answers.getParent()).setVisibility(View.GONE);
            else answers.setVisibility(View.VISIBLE);
            answers.setTextAppearance(mActivity, android.R.style.TextAppearance_DeviceDefault);
            answers.setTextColor(mActivity.getResources().getColor(R.color.colorAccent));
            answersString.setSpan(new ForegroundColorSpan(
                            mActivity.getResources().getColor(R.color.colorAccent)),
                    0, answersString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            answers.setText(answersString);
        } else {
            if (mAnswers.containsKey(number)) {
                answers.setVisibility(View.VISIBLE);
                SpannableStringBuilder answerStringBuilder
                        = new SpannableStringBuilder(mActivity.getString(R.string.answers_text));
                answerStringBuilder.setSpan(new StyleSpan(android.graphics.Typeface.ITALIC),
                        0, answerStringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                answerStringBuilder.append("  ");
                for (String answer : mAnswers.get(number)) {
                    answerStringBuilder.append(">>");
                    answerStringBuilder.append(answer);
                    answerStringBuilder.setSpan(
                            new ForegroundColorSpan(mActivity.getResources().getColor(R.color.colorAccent)),
                            answerStringBuilder.length() - answer.length() - 2, answerStringBuilder.length(),
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    answerStringBuilder.setSpan(new UnderlineSpan(),
                            answerStringBuilder.length() - answer.length() - 2, answerStringBuilder.length(),
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    answerStringBuilder.append(",  ");
                }

                SpannableString spannableAnswersString = SpannableString.valueOf(
                        answerStringBuilder.delete(answerStringBuilder.length() - 3, answerStringBuilder.length()));
                spannableAnswersString = setAnswersBackgroundSpansIfNeeded(spannableAnswersString, position);
                answers.setText(spannableAnswersString);

            } else answers.setVisibility(View.GONE);
        }
    }

    private int getAnswersCountForPost(String number) {
        if (mAnswers.containsKey(number)) return mAnswers.get(number).size();
        else return 0;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Post post = mActivity.mPosts.get(position);

        final String number = post.getNum();
        String time = post.getDate();
        String op = post.getOp();
        String name = post.getName();
        String trip = post.getTrip();
        String subject = post.getSubject();
        String comment = post.getComment();
        List<Files> files = post.getFiles();

        holder.numberAndTime.setText(StringUtils.INSTANCE.getNumberAndTimeString(
                mActivity, position, number, op, name, trip, time));
        if (!boardId.equals("b")) holder.subject.setText(Html.fromHtml(subject));
        else holder.subject.setVisibility(View.GONE);
        if (subject.equals("")) holder.subject.setVisibility(View.GONE);
        holder.comment.setText(getCorrectStringForCommentTextView(comment, position));
        holder.comment.setMovementMethod(CommentLinkMovementMethod.getInstance(mActivity, position));
        setCorrectStringForAnswersTextView(holder, number, position);
        holder.answers.setMovementMethod(AnswersLinkMovementMethod.getInstance(mActivity, position));
        if (position == 0) {
            if (subject.equals("")) ((TextView)mActivity.toolbar.findViewById(R.id.title)).setText(Html.fromHtml(comment));
            else ((TextView)mActivity.toolbar.findViewById(R.id.title)).setText(Html.fromHtml(subject));
        }

        String width;
        String height;
        String thumbnail;
        String size;

        int filesSize = files.size();
        switchImagesVisibility(holder, filesSize);

        for (int i = 0; i < filesSize; i++) {
            Files file = files.get(i);

            width = file.getWidth();
            height = file.getHeight();
            thumbnail = file.getThumbnail();
            size = file.getSize();

            if (files.size() == 1) {
                setupImageContainer(holder.image, holder.webmImageView, holder.summary,
                        thumbnail, file, size, width, height);
            }
            if (files.size() > 1) {
                switch (i) {
                    case 0: {
                        setupImageContainer(holder.image1, holder.webmImageView1,
                                holder.summary1, thumbnail, file, size, width, height); break;
                    }
                    case 1: {
                        setupImageContainer(holder.image2, holder.webmImageView2,
                                holder.summary2, thumbnail, file, size, width, height); break;
                    }
                    case 2: {
                        setupImageContainer(holder.image3, holder.webmImageView3,
                                holder.summary3, thumbnail, file, size, width, height); break;
                    }
                    case 3: {
                        setupImageContainer(holder.image4, holder.webmImageView4,
                                holder.summary4, thumbnail, file, size, width, height); break;
                    }
                    case 4: {
                        setupImageContainer(holder.image5, holder.webmImageView5,
                                holder.summary5, thumbnail, file, size, width, height); break;
                    }
                    case 5: {
                        setupImageContainer(holder.image6, holder.webmImageView6,
                                holder.summary6, thumbnail, file, size, width, height); break;
                    }
                    case 6: {
                        setupImageContainer(holder.image7, holder.webmImageView7,
                                holder.summary7, thumbnail, file, size, width, height); break;
                    }
                    case 7: {
                        setupImageContainer(holder.image8, holder.webmImageView8,
                                holder.summary8, thumbnail, file, size, width, height); break;
                    }
                }
            }
        }
    }

    private void switchImagesVisibility(SingleThreadRecyclerViewAdapter.ViewHolder holder, int filesSize) {
        switchImagesVisibility(
                holder.imageAndSummaryContainer1, holder.imageAndSummaryContainer2,
                holder.imageAndSummaryContainer3, holder.imageAndSummaryContainer4,
                holder.imageAndSummaryContainer5, holder.imageAndSummaryContainer6,
                holder.imageAndSummaryContainer7, holder.imageAndSummaryContainer8,
                filesSize);
//        switch (filesSize) {
//            case 1: {
//                if (holder.imageAndSummaryContainer1 != null)
//                    holder.imageAndSummaryContainer1.setVisibility(View.VISIBLE);
//                if (holder.imageAndSummaryContainer2 != null)
//                    holder.imageAndSummaryContainer2.setVisibility(View.GONE);
//                if (holder.imageAndSummaryContainer3 != null)
//                    holder.imageAndSummaryContainer3.setVisibility(View.GONE);
//                if (holder.imageAndSummaryContainer4 != null)
//                    holder.imageAndSummaryContainer4.setVisibility(View.GONE);
//                break;
//            }
//            case 2: {
//                if (holder.imageAndSummaryContainer1 != null)
//                    holder.imageAndSummaryContainer1.setVisibility(View.VISIBLE);
//                if (holder.imageAndSummaryContainer2 != null)
//                    holder.imageAndSummaryContainer2.setVisibility(View.VISIBLE);
//                if (holder.imageAndSummaryContainer3 != null)
//                    holder.imageAndSummaryContainer3.setVisibility(View.GONE);
//                if (holder.imageAndSummaryContainer4 != null)
//                    holder.imageAndSummaryContainer4.setVisibility(View.GONE);
//                break;
//            }
//            case 3: {
//                if (holder.imageAndSummaryContainer1 != null)
//                    holder.imageAndSummaryContainer1.setVisibility(View.VISIBLE);
//                if (holder.imageAndSummaryContainer2 != null)
//                    holder.imageAndSummaryContainer2.setVisibility(View.VISIBLE);
//                if (holder.imageAndSummaryContainer3 != null)
//                    holder.imageAndSummaryContainer3.setVisibility(View.VISIBLE);
//                if (holder.imageAndSummaryContainer4 != null)
//                    holder.imageAndSummaryContainer4.setVisibility(View.GONE);
//                break;
//            }
//            case 4: {
//                if (holder.imageAndSummaryContainer1 != null)
//                    holder.imageAndSummaryContainer1.setVisibility(View.VISIBLE);
//                if (holder.imageAndSummaryContainer2 != null)
//                    holder.imageAndSummaryContainer2.setVisibility(View.VISIBLE);
//                if (holder.imageAndSummaryContainer3 != null)
//                    holder.imageAndSummaryContainer3.setVisibility(View.VISIBLE);
//                if (holder.imageAndSummaryContainer4 != null)
//                    holder.imageAndSummaryContainer4.setVisibility(View.VISIBLE);
//                break;
//            }
//        }
    }

    private void switchImagesVisibility(RelativeLayout imageAndSummaryContainer1,
                                        RelativeLayout imageAndSummaryContainer2,
                                        RelativeLayout imageAndSummaryContainer3,
                                        RelativeLayout imageAndSummaryContainer4,
                                        RelativeLayout imageAndSummaryContainer5,
                                        RelativeLayout imageAndSummaryContainer6,
                                        RelativeLayout imageAndSummaryContainer7,
                                        RelativeLayout imageAndSummaryContainer8,
                                        int filesSize) {
        switch (filesSize) {
            case 1: {
                if (imageAndSummaryContainer1 != null)
                    imageAndSummaryContainer1.setVisibility(View.VISIBLE);
                if (imageAndSummaryContainer2 != null)
                    imageAndSummaryContainer2.setVisibility(View.GONE);
                if (imageAndSummaryContainer3 != null)
                    imageAndSummaryContainer3.setVisibility(View.GONE);
                if (imageAndSummaryContainer4 != null)
                    imageAndSummaryContainer4.setVisibility(View.GONE);
                if (imageAndSummaryContainer5 != null)
                    imageAndSummaryContainer5.setVisibility(View.GONE);
                if (imageAndSummaryContainer6 != null)
                    imageAndSummaryContainer6.setVisibility(View.GONE);
                if (imageAndSummaryContainer7 != null)
                    imageAndSummaryContainer7.setVisibility(View.GONE);
                if (imageAndSummaryContainer8 != null)
                    imageAndSummaryContainer8.setVisibility(View.GONE);
                break;
            }
            case 2: {
                if (imageAndSummaryContainer1 != null)
                    imageAndSummaryContainer1.setVisibility(View.VISIBLE);
                if (imageAndSummaryContainer2 != null)
                    imageAndSummaryContainer2.setVisibility(View.VISIBLE);
                if (imageAndSummaryContainer3 != null)
                    imageAndSummaryContainer3.setVisibility(View.GONE);
                if (imageAndSummaryContainer4 != null)
                    imageAndSummaryContainer4.setVisibility(View.GONE);
                if (imageAndSummaryContainer5 != null)
                    imageAndSummaryContainer5.setVisibility(View.GONE);
                if (imageAndSummaryContainer6 != null)
                    imageAndSummaryContainer6.setVisibility(View.GONE);
                if (imageAndSummaryContainer7 != null)
                    imageAndSummaryContainer7.setVisibility(View.GONE);
                if (imageAndSummaryContainer8 != null)
                    imageAndSummaryContainer8.setVisibility(View.GONE);
                break;
            }
            case 3: {
                if (imageAndSummaryContainer1 != null)
                    imageAndSummaryContainer1.setVisibility(View.VISIBLE);
                if (imageAndSummaryContainer2 != null)
                    imageAndSummaryContainer2.setVisibility(View.VISIBLE);
                if (imageAndSummaryContainer3 != null)
                    imageAndSummaryContainer3.setVisibility(View.VISIBLE);
                if (imageAndSummaryContainer4 != null)
                    imageAndSummaryContainer4.setVisibility(View.GONE);
                if (imageAndSummaryContainer5 != null)
                    imageAndSummaryContainer5.setVisibility(View.GONE);
                if (imageAndSummaryContainer6 != null)
                    imageAndSummaryContainer6.setVisibility(View.GONE);
                if (imageAndSummaryContainer7 != null)
                    imageAndSummaryContainer7.setVisibility(View.GONE);
                if (imageAndSummaryContainer8 != null)
                    imageAndSummaryContainer8.setVisibility(View.GONE);
                break;
            }
            case 4: {
                if (imageAndSummaryContainer1 != null)
                    imageAndSummaryContainer1.setVisibility(View.VISIBLE);
                if (imageAndSummaryContainer2 != null)
                    imageAndSummaryContainer2.setVisibility(View.VISIBLE);
                if (imageAndSummaryContainer3 != null)
                    imageAndSummaryContainer3.setVisibility(View.VISIBLE);
                if (imageAndSummaryContainer4 != null)
                    imageAndSummaryContainer4.setVisibility(View.VISIBLE);
                if (imageAndSummaryContainer5 != null)
                    imageAndSummaryContainer5.setVisibility(View.GONE);
                if (imageAndSummaryContainer6 != null)
                    imageAndSummaryContainer6.setVisibility(View.GONE);
                if (imageAndSummaryContainer7 != null)
                    imageAndSummaryContainer7.setVisibility(View.GONE);
                if (imageAndSummaryContainer8 != null)
                    imageAndSummaryContainer8.setVisibility(View.GONE);
                break;
            }
            case 5: {
                if (imageAndSummaryContainer1 != null)
                    imageAndSummaryContainer1.setVisibility(View.VISIBLE);
                if (imageAndSummaryContainer2 != null)
                    imageAndSummaryContainer2.setVisibility(View.VISIBLE);
                if (imageAndSummaryContainer3 != null)
                    imageAndSummaryContainer3.setVisibility(View.VISIBLE);
                if (imageAndSummaryContainer4 != null)
                    imageAndSummaryContainer4.setVisibility(View.VISIBLE);
                if (imageAndSummaryContainer5 != null)
                    imageAndSummaryContainer5.setVisibility(View.VISIBLE);
                if (imageAndSummaryContainer6 != null)
                    imageAndSummaryContainer6.setVisibility(View.GONE);
                if (imageAndSummaryContainer7 != null)
                    imageAndSummaryContainer7.setVisibility(View.GONE);
                if (imageAndSummaryContainer8 != null)
                    imageAndSummaryContainer8.setVisibility(View.GONE);
                break;
            }
            case 6: {
                if (imageAndSummaryContainer1 != null)
                    imageAndSummaryContainer1.setVisibility(View.VISIBLE);
                if (imageAndSummaryContainer2 != null)
                    imageAndSummaryContainer2.setVisibility(View.VISIBLE);
                if (imageAndSummaryContainer3 != null)
                    imageAndSummaryContainer3.setVisibility(View.VISIBLE);
                if (imageAndSummaryContainer4 != null)
                    imageAndSummaryContainer4.setVisibility(View.VISIBLE);
                if (imageAndSummaryContainer5 != null)
                    imageAndSummaryContainer5.setVisibility(View.VISIBLE);
                if (imageAndSummaryContainer6 != null)
                    imageAndSummaryContainer6.setVisibility(View.VISIBLE);
                if (imageAndSummaryContainer7 != null)
                    imageAndSummaryContainer7.setVisibility(View.GONE);
                if (imageAndSummaryContainer8 != null)
                    imageAndSummaryContainer8.setVisibility(View.GONE);
                break;
            }
            case 7: {
                if (imageAndSummaryContainer1 != null)
                    imageAndSummaryContainer1.setVisibility(View.VISIBLE);
                if (imageAndSummaryContainer2 != null)
                    imageAndSummaryContainer2.setVisibility(View.VISIBLE);
                if (imageAndSummaryContainer3 != null)
                    imageAndSummaryContainer3.setVisibility(View.VISIBLE);
                if (imageAndSummaryContainer4 != null)
                    imageAndSummaryContainer4.setVisibility(View.VISIBLE);
                if (imageAndSummaryContainer5 != null)
                    imageAndSummaryContainer5.setVisibility(View.VISIBLE);
                if (imageAndSummaryContainer6 != null)
                    imageAndSummaryContainer6.setVisibility(View.VISIBLE);
                if (imageAndSummaryContainer7 != null)
                    imageAndSummaryContainer7.setVisibility(View.VISIBLE);
                if (imageAndSummaryContainer8 != null)
                    imageAndSummaryContainer8.setVisibility(View.GONE);
                break;
            }
            case 8: {
                if (imageAndSummaryContainer1 != null)
                    imageAndSummaryContainer1.setVisibility(View.VISIBLE);
                if (imageAndSummaryContainer2 != null)
                    imageAndSummaryContainer2.setVisibility(View.VISIBLE);
                if (imageAndSummaryContainer3 != null)
                    imageAndSummaryContainer3.setVisibility(View.VISIBLE);
                if (imageAndSummaryContainer4 != null)
                    imageAndSummaryContainer4.setVisibility(View.VISIBLE);
                if (imageAndSummaryContainer5 != null)
                    imageAndSummaryContainer5.setVisibility(View.VISIBLE);
                if (imageAndSummaryContainer6 != null)
                    imageAndSummaryContainer6.setVisibility(View.VISIBLE);
                if (imageAndSummaryContainer7 != null)
                    imageAndSummaryContainer7.setVisibility(View.VISIBLE);
                if (imageAndSummaryContainer8 != null)
                    imageAndSummaryContainer8.setVisibility(View.VISIBLE);
                break;
            }
        }
    }


    private void setupImageContainer(ImageView image, ImageView webmImageView,
                                     TextView summary, String thumbnail,
                                     final Files file, String imageOrVideoSize, String imageOrVideoWidth,
                                     String imageOrVideoHeight) {

        loadThumbnailPreview(thumbnail, image, imageOrVideoWidth, imageOrVideoHeight);

        View.OnClickListener thumbnailClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFullPicVid(file);
            }
        };

        if (file.getPath().substring(file.getPath().length() - 4,
                file.getPath().length()).equals(Formats.WEBM)) {
            webmImageView.setVisibility(View.VISIBLE);
            webmImageView.setOnClickListener(thumbnailClickListener);
        } else webmImageView.setVisibility(View.GONE);

        image.setOnClickListener(thumbnailClickListener);
        summary.setText(StringUtils.INSTANCE.getSummaryString(mActivity, imageOrVideoSize,
                imageOrVideoWidth, imageOrVideoHeight));
    }

    private void loadThumbnailPreview(String thumbnail, final ImageView image,
                                      final String width, final String height) {
        setImageViewWidthDependingOnOrientation(mActivity.getResources().getConfiguration(), image);
        image.setImageBitmap(null);
        if (image.getAnimation() != null) image.getAnimation().cancel();
        image.setBackgroundColor(mActivity.getResources().getColor(R.color.dark_gray));

        Glide.with(mActivity).load(Uri.parse(Constants.DVACH_BASE_URL + thumbnail)).asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.NONE).listener(new RequestListener<Uri, Bitmap>() {
            @Override
            public boolean onException(Exception e, Uri model, Target<Bitmap> target,
                                       boolean isFirstResource) {
                Log.d(LOG_TAG, "onException:");
                e.printStackTrace();
                return false;
            }

            @Override
            public boolean onResourceReady(final Bitmap resource, Uri model,
                                           Target<Bitmap> target, boolean isFromMemoryCache,
                                           boolean isFirstResource) {
                Log.d(LOG_TAG, "onResourceReady: ");
                int widthInt = Integer.parseInt(width);
                int heightInt = Integer.parseInt(height);
                float aspectRatio = ((float) widthInt / (float) heightInt);
                final int finalHeight = Math.round(image.getLayoutParams().width / aspectRatio);
                Log.d(LOG_TAG, "aspect ratio: " + aspectRatio);

                image.post(new Runnable() {
                    @Override
                    public void run() {
                        image.startAnimation(com.koresuniku.wishmaster.util.AnimationUtils.resizeThumbnail(
                                image, resource,  image.getHeight(), finalHeight));
                    }
                });
                return false;
            }
        }).into(image);


    }


    public View getViewForPosition(final int position) {
        singleViewContainer = new FrameLayout(mActivity);
        singleView = inflateProperView(getItemViewType(position));
        TextView numberAndTimeTextView = (TextView) singleView.findViewById(R.id.post_number_and_time_info);
        TextView subjectTextView = (TextView) singleView.findViewById(R.id.post_subject);
        TextView commentTextView = (TextView) singleView.findViewById(R.id.post_comment);
        TextView answersTextView = (TextView) singleView.findViewById(R.id.answers);
        RelativeLayout imageAndSummaryContainer1 = null;
        RelativeLayout imageAndSummaryContainer2 = null;
        RelativeLayout imageAndSummaryContainer3 = null;
        RelativeLayout imageAndSummaryContainer4 = null;
        RelativeLayout imageAndSummaryContainer5 = null;
        RelativeLayout imageAndSummaryContainer6 = null;
        RelativeLayout imageAndSummaryContainer7 = null;
        RelativeLayout imageAndSummaryContainer8 = null;
        ImageView image = null;
        ImageView image1 = null;
        ImageView image2 = null;
        ImageView image3 = null;
        ImageView image4 = null;
        ImageView image5 = null;
        ImageView image6 = null;
        ImageView image7 = null;
        ImageView image8 = null;
        ImageView webmImageView = null;
        ImageView webmImageView1 = null;
        ImageView webmImageView2 = null;
        ImageView webmImageView3 = null;
        ImageView webmImageView4 = null;
        ImageView webmImageView5 = null;
        ImageView webmImageView6 = null;
        ImageView webmImageView7 = null;
        ImageView webmImageView8 = null;
        TextView summary = null;
        TextView summary1 = null;
        TextView summary2 = null;
        TextView summary3 = null;
        TextView summary4 = null;
        TextView summary5 = null;
        TextView summary6 = null;
        TextView summary7 = null;
        TextView summary8 = null;
        if (getItemViewType(position) == Constants.ITEM_SINGLE_IMAGE) {
            image = (ImageView) singleView.findViewById(R.id.post_image);
            webmImageView = (ImageView) singleView.findViewById(R.id.webm_imageview);
            summary = (TextView) singleView.findViewById(R.id.image_summary);
        }
        if (getItemViewType(position) == Constants.ITEM_MULTIPLE_IMAGES) {
            imageAndSummaryContainer1 = (RelativeLayout) singleView.findViewById(R.id.image_with_summary_container_1);
            imageAndSummaryContainer2 = (RelativeLayout) singleView.findViewById(R.id.image_with_summary_container_2);
            imageAndSummaryContainer3 = (RelativeLayout) singleView.findViewById(R.id.image_with_summary_container_3);
            imageAndSummaryContainer4 = (RelativeLayout) singleView.findViewById(R.id.image_with_summary_container_4);
            imageAndSummaryContainer5 = (RelativeLayout) singleView.findViewById(R.id.image_with_summary_container_5);
            imageAndSummaryContainer6 = (RelativeLayout) singleView.findViewById(R.id.image_with_summary_container_6);
            imageAndSummaryContainer7 = (RelativeLayout) singleView.findViewById(R.id.image_with_summary_container_7);
            imageAndSummaryContainer8 = (RelativeLayout) singleView.findViewById(R.id.image_with_summary_container_8);
            image1 = (ImageView) singleView.findViewById(R.id.post_image_1);
            webmImageView1 = (ImageView) singleView.findViewById(R.id.webm_imageview_1);
            image2 = (ImageView) singleView.findViewById(R.id.post_image_2);
            webmImageView2 = (ImageView) singleView.findViewById(R.id.webm_imageview_2);
            image3 = (ImageView) singleView.findViewById(R.id.post_image_3);
            webmImageView3 = (ImageView) singleView.findViewById(R.id.webm_imageview_3);
            image4 = (ImageView) singleView.findViewById(R.id.post_image_4);
            webmImageView4 = (ImageView) singleView.findViewById(R.id.webm_imageview_4);
            image5 = (ImageView) singleView.findViewById(R.id.post_image_5);
            webmImageView5 = (ImageView) singleView.findViewById(R.id.webm_imageview_5);
            image6 = (ImageView) singleView.findViewById(R.id.post_image_6);
            webmImageView6 = (ImageView) singleView.findViewById(R.id.webm_imageview_6);
            image7 = (ImageView) singleView.findViewById(R.id.post_image_7);
            webmImageView7 = (ImageView) singleView.findViewById(R.id.webm_imageview_7);
            image8 = (ImageView) singleView.findViewById(R.id.post_image_8);
            webmImageView8 = (ImageView) singleView.findViewById(R.id.webm_imageview_8);
            summary1 = (TextView) singleView.findViewById(R.id.image_summary_1);
            summary2 = (TextView) singleView.findViewById(R.id.image_summary_2);
            summary3 = (TextView) singleView.findViewById(R.id.image_summary_3);
            summary4 = (TextView) singleView.findViewById(R.id.image_summary_4);
            summary5 = (TextView) singleView.findViewById(R.id.image_summary_5);
            summary6 = (TextView) singleView.findViewById(R.id.image_summary_6);
            summary7 = (TextView) singleView.findViewById(R.id.image_summary_7);
            summary8 = (TextView) singleView.findViewById(R.id.image_summary_8);
        }
        Post post = mActivity.mPosts.get(position);

        final String number = post.getNum();
        String time = post.getDate();
        String op = post.getOp();
        String name = post.getName();
        String trip = post.getTrip();
        String subject = post.getSubject();
        String comment = post.getComment();
        List<Files> files = post.getFiles();

        numberAndTimeTextView.setText(StringUtils.INSTANCE.getNumberAndTimeString(
                mActivity, position, number, op, name, trip, time));
        if (!boardId.equals("b")) subjectTextView.setText(Html.fromHtml(subject));
        else subjectTextView.setVisibility(View.GONE);
        if (subject.equals(""))subjectTextView.setVisibility(View.GONE);
        commentTextView.setText(getCorrectStringForCommentTextView(comment, position));
        commentTextView.setMovementMethod(CommentLinkMovementMethod.getInstance(mActivity, position));
        commentTextView.setText(getCorrectStringForCommentTextView(comment, position));
        setCorrectStringForAnswersTextView(answersTextView, number, position);
        answersTextView.setMovementMethod(AnswersLinkMovementMethod.getInstance(mActivity, position));
        if (position == 0) {
            if (subject.equals("")) ((TextView)mActivity.toolbar.findViewById(R.id.title)).setText(Html.fromHtml(comment));
            else ((TextView)mActivity.toolbar.findViewById(R.id.title)).setText(Html.fromHtml(subject));
        }

        String width;
        String height;
        String thumbnail;
        String size;

        int filesSize = files.size();
        switchImagesVisibility(
                imageAndSummaryContainer1, imageAndSummaryContainer2,
                imageAndSummaryContainer3, imageAndSummaryContainer4,
                imageAndSummaryContainer5, imageAndSummaryContainer6,
                imageAndSummaryContainer7, imageAndSummaryContainer8,
                filesSize);

        for (int i = 0; i < filesSize; i++) {
            Files file = files.get(i);

            width = file.getWidth();
            height = file.getHeight();
            thumbnail = file.getThumbnail();
            size = file.getSize();

            if (files.size() == 1) {
                setupImageContainer(image,webmImageView, summary, thumbnail, file, size, width, height);
            }
            if (files.size() > 1) {
                switch (i) {
                    case 0: {
                        setupImageContainer(image1, webmImageView1,
                                summary1, thumbnail, file, size, width, height); break;
                    }
                    case 1: {
                        setupImageContainer(image2, webmImageView2,
                                summary2, thumbnail, file, size, width, height); break;
                    }
                    case 2: {
                        setupImageContainer(image3, webmImageView3,
                                summary3, thumbnail, file, size, width, height); break;
                    }
                    case 3: {
                        setupImageContainer(image4, webmImageView4,
                                summary4, thumbnail, file, size, width, height); break;
                    }
                    case 4: {
                        setupImageContainer(image5, webmImageView5,
                                summary5, thumbnail, file, size, width, height); break;
                    }
                    case 5: {
                        setupImageContainer(image6, webmImageView6,
                                summary6, thumbnail, file, size, width, height); break;
                    }
                    case 6: {
                        setupImageContainer(image7, webmImageView7,
                                summary7, thumbnail, file, size, width, height); break;
                    }
                    case 7: {
                        setupImageContainer(image8, webmImageView8,
                                summary8, thumbnail, file, size, width, height); break;
                    }
                }
            }
        }

        singleViewContainer.addView(singleView);
        return singleViewContainer;

    }

    @SuppressLint("UseSparseArrays")
    private void showFullPicVid(Files file) {
        if (DeviceUtils.sdkIsKitkatOrHigher()) {
            UiUtils.showSystemUI(mActivity);
            UiUtils.setBarsTranslucent(mActivity, true);
        }
        mActivity.fullPicVidOpenedAndFullScreenModeIsOn = false;
        mActivity.picVidToolbarContainer.setVisibility(View.VISIBLE);
        mActivity.fullPicVidOpened = true;

        SingleThreadActivity.imageCachePaths = new ArrayList<>();
        SingleThreadActivity.galleryFragments = new HashMap<>();

        UiUtils.barsAreShown = true;

        SingleThreadActivity.files = new ArrayList<>();
        for (Post post : mActivity.mPosts) {
            SingleThreadActivity.files.addAll(post.getFiles());
        }
        final int currentPosition = SingleThreadActivity.files.indexOf(file);

        mActivity.picVidPagerAdapter = new PicVidPagerAdapter(
                mActivity.getSupportFragmentManager(),
                mActivity, SingleThreadActivity.files, currentPosition);

        mActivity.picVidPager.setAdapter(mActivity.picVidPagerAdapter);
        mActivity.picVidPager.setOffscreenPageLimit(1);
        mActivity.picVidPager.setCurrentItem(currentPosition);
        mActivity.picVidOpenedPosition = currentPosition;
        mActivity.fullPicVidContainer.setVisibility(View.VISIBLE);
        mActivity.singleThreadViewPagerOnPageChangeListener = new SingleThreadViewPagerOnPageChangeListener(mActivity);
        mActivity.picVidPager.addOnPageChangeListener(mActivity.singleThreadViewPagerOnPageChangeListener);

        String displayName = SingleThreadActivity.files.get(currentPosition).getDisplayName();
        if (displayName == null || displayName.equals("") || displayName.equals(" ")) {
            mActivity.picVidToolbarTitleTextView.setText(mActivity.getString(R.string.noname_text));
        } else {
            mActivity.picVidToolbarTitleTextView.setText(displayName);
        }
        mActivity.picVidToolbarTitleTextView.setTypeface(Typeface.DEFAULT_BOLD);
        mActivity.picVidToolbarTitleTextView.setTextSize(mActivity.getResources().getDimension(R.dimen.media_toolbar_text_size));
        mActivity.picVidToolbarShortInfoTextView.setText(StringUtils.INSTANCE.getShortInfoForToolbarString(
                mActivity.picVidToolbarShortInfoTextView, currentPosition, SingleThreadActivity.files));

        mActivity.picVidToolbarMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Log.d(LOG_TAG, "onMenuItemClick");
                mActivity.picVidToolbarUrl =
                        Constants.DVACH_BASE_URL + SingleThreadActivity.files.get(currentPosition).getPath();
                mActivity.picVidToolbarFilename =
                        SingleThreadActivity.files.get(currentPosition).getDisplayName();
                mActivity.mFileSaver.saveFileToExternalStorage(
                        mActivity.picVidToolbarUrl, mActivity.picVidToolbarFilename);
                return false;
            }
        });
    }

    private void setImageViewWidthDependingOnOrientation(
            Configuration configuration, ImageView image) {
        if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            image.getLayoutParams().width =
                    (int) mActivity.getResources().getDimension(R.dimen.thumbnail_width_vertical);
            image.getLayoutParams().height =
                    (int) mActivity.getResources().getDimension(R.dimen.thumbnail_width_vertical);
            image.requestLayout();
        } else {
            image.getLayoutParams().width =
                    (int) mActivity.getResources().getDimension(R.dimen.thumbnail_width_horizontal);
            image.getLayoutParams().height =
                    (int) mActivity.getResources().getDimension(R.dimen.thumbnail_width_horizontal);
            image.requestLayout();
        }
    }

    public void notifyNewPosts(int before, int after) {
        String toShow = StringUtils.INSTANCE.getNotifyNewPostsString(after - before);
        if (mActivity.mNewPostsNotifierToast != null) mActivity.mNewPostsNotifierToast.cancel();
        mActivity.mNewPostsNotifierToast = Toast.makeText(mActivity, toShow, Toast.LENGTH_SHORT);
        mActivity.mNewPostsNotifierToast.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mActivity.mNewPostsNotifierToast.cancel();
            }
        }, 500);

    }

    @Override
    public int getItemCount() {
      return mActivity.mPosts.size();
    }

    @Override
    public int getItemViewType(int position) {
        List<Files> files = mActivity.mPosts.get(position).getFiles();
        if (files != null) {
            if (files.size() == 0) {
                return Constants.ITEM_NO_IMAGES;
            }
            if (files.size() == 1) {
                return Constants.ITEM_SINGLE_IMAGE;
            }
            if (files.size() > 1) {
                return Constants.ITEM_MULTIPLE_IMAGES;
            }
        }
        return -1;
    }
}
