package com.koresuniku.wishmaster.adapters;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.koresuniku.wishmaster.R;
import com.koresuniku.wishmaster.activities.SingleThreadActivity;
import com.koresuniku.wishmaster.http.single_thread_api.models.Post;
import com.koresuniku.wishmaster.http.threads_api.models.Files;
import com.koresuniku.wishmaster.ui.UIUtils;
import com.koresuniku.wishmaster.ui.text.AnswersLinkMovementMethod;
import com.koresuniku.wishmaster.ui.text.CommentLinkMovementMethod;
import com.koresuniku.wishmaster.ui.views.NoScrollTextView;
import com.koresuniku.wishmaster.utils.Constants;
import com.koresuniku.wishmaster.utils.DeviceUtils;
import com.koresuniku.wishmaster.utils.Formats;
import com.koresuniku.wishmaster.utils.StringUtils;
import com.koresuniku.wishmaster.utils.listeners.SingleThreadViewPagerOnPageChangeListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SingleThreadRecyclerViewAdapter extends RecyclerView.Adapter<SingleThreadRecyclerViewAdapter.ViewHolder> {
    private final String LOG_TAG = SingleThreadRecyclerViewAdapter.class.getSimpleName();

    private SingleThreadRecyclerViewAdapter thisAdapter = this;
    private SingleThreadActivity mActivity;
    private Map<String, List<String>> mAnswers;
    public List<List<Integer>> mAnswersSpansLocations;
    public BackgroundColorSpan backgroundColorSpan;
    public ForegroundColorSpan foregroundColorSpan;
    private String boardId;
    private int mViewType;

    public SingleThreadRecyclerViewAdapter(SingleThreadActivity activity, String board) {
        mActivity = activity;
        mAnswers = new HashMap<>();
        backgroundColorSpan = new BackgroundColorSpan(mActivity.getResources().getColor(R.color.link_background));
        foregroundColorSpan = new ForegroundColorSpan(mActivity.getResources().getColor(R.color.link_background));
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
        private FrameLayout headerContainer;
        private LinearLayout threadItemContainer;
        private TextView numberAndTime;
        private TextView subject;
        private LinearLayout imagiesAndSummariesOver4ImagesContainer;
        private LinearLayout imagiesAndSummariesContainer;
        private LinearLayout imageAndSummaryContainer;
        private LinearLayout imageAndSummaryContainer1;
        private LinearLayout imageAndSummaryContainer2;
        private LinearLayout imageAndSummaryContainer3;
        private LinearLayout imageAndSummaryContainer4;
        private ImageView image;
        private ImageView image1;
        private ImageView image2;
        private ImageView image3;
        private ImageView image4;
        private ImageView webmImageView;
        private ImageView webmImageView1;
        private ImageView webmImageView2;
        private ImageView webmImageView3;
        private ImageView webmImageView4;
        private TextView summary;
        private TextView summary1;
        private TextView summary2;
        private TextView summary3;
        private TextView summary4;
        private TextView comment;
        private NoScrollTextView answers;
        private Bitmap webmBitmap;

        public ViewHolder(View itemView) {
            super(itemView);

            numberAndTime = (TextView) itemView.findViewById(R.id.post_number_and_time_info);
            subject = (TextView) itemView.findViewById(R.id.post_subject);
            if (mViewType == Constants.ITEM_SINGLE_IMAGE) {
                threadItemContainer =
                        (LinearLayout) itemView.findViewById(R.id.post_item_single_image_container);
                imageAndSummaryContainer =
                        (LinearLayout) itemView.findViewById(R.id.image_with_summary_container);
                image = (ImageView) itemView.findViewById(R.id.post_image);
                webmImageView = (ImageView) itemView.findViewById(R.id.webm_imageview);
                summary = (TextView) itemView.findViewById(R.id.image_summary);
            } else if (mViewType == Constants.ITEM_MULTIPLE_IMAGES) {
                threadItemContainer =
                        (LinearLayout) itemView.findViewById(R.id.post_item_multiple_image_container);
                imagiesAndSummariesContainer =
                        (LinearLayout) itemView.findViewById(R.id.images_with_summaries_container);
                imagiesAndSummariesOver4ImagesContainer =
                        (LinearLayout) itemView
                                .findViewById(R.id.imagies_and_summaries_over_4_images_container);
                imageAndSummaryContainer1 =
                        (LinearLayout) itemView.findViewById(R.id.image_with_summary_container_1);
                imageAndSummaryContainer2 =
                        (LinearLayout) itemView.findViewById(R.id.image_with_summary_container_2);
                imageAndSummaryContainer3 =
                        (LinearLayout) itemView.findViewById(R.id.image_with_summary_container_3);
                imageAndSummaryContainer4 =
                        (LinearLayout) itemView.findViewById(R.id.image_with_summary_container_4);
                image1 = (ImageView) itemView.findViewById(R.id.post_image_1);
                webmImageView1 = (ImageView) itemView.findViewById(R.id.webm_imageview_1);
                image2 = (ImageView) itemView.findViewById(R.id.post_image_2);
                webmImageView2 = (ImageView) itemView.findViewById(R.id.webm_imageview_2);
                image3 = (ImageView) itemView.findViewById(R.id.post_image_3);
                webmImageView3 = (ImageView) itemView.findViewById(R.id.webm_imageview_3);
                image4 = (ImageView) itemView.findViewById(R.id.post_image_4);
                webmImageView4 = (ImageView) itemView.findViewById(R.id.webm_imageview_4);
                summary1 = (TextView) itemView.findViewById(R.id.image_summary_1);
                summary2 = (TextView) itemView.findViewById(R.id.image_summary_2);
                summary3 = (TextView) itemView.findViewById(R.id.image_summary_3);
                summary4 = (TextView) itemView.findViewById(R.id.image_summary_4);
            } else if (mViewType == Constants.ITEM_NO_IMAGES) {
                threadItemContainer =
                        (LinearLayout) itemView.findViewById(R.id.post_item_no_images_container);
            }
            comment = (TextView) itemView.findViewById(R.id.post_comment);
            answers = (NoScrollTextView) itemView.findViewById(R.id.answers);
            webmBitmap = BitmapFactory.decodeResource(mActivity.getResources(), R.drawable.webm);
            setIsRecyclable(true);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case Constants.ITEM_NO_IMAGES: {
                mViewType = viewType;
                view = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.post_item_no_images, parent, false);
                return new ViewHolder(view);
            }
            case Constants.ITEM_SINGLE_IMAGE: {
                mViewType = viewType;
                view = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.post_item_single_image, parent, false);
                return new ViewHolder(view);
            }
            case Constants.ITEM_MULTIPLE_IMAGES: {
                mViewType = viewType;
                view = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.post_item_multiple_images, parent, false);
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
                        .inflate(R.layout.post_item_no_images, null, false);
                return view;
            }
            case Constants.ITEM_SINGLE_IMAGE: {
                mViewType = viewType;
                view = LayoutInflater
                        .from(mActivity)
                        .inflate(R.layout.post_item_single_image, null, false);
                return view;
            }
            case Constants.ITEM_MULTIPLE_IMAGES: {
                mViewType = viewType;
                view = LayoutInflater
                        .from(mActivity)
                        .inflate(R.layout.post_item_multiple_images, null, false);
                return view;
            }
        }
        return null;
    }

    private void ornutVGolosinu() {
        Toast toast = Toast.makeText(mActivity, "GIFKA!!!!!!", Toast.LENGTH_SHORT);
        toast.show();
    }

    private void getAnswersForPost() {
        Pattern p = Pattern.compile("href=\"(.*?)\"");
        List<String> postAnswers;
        for (Post post : SingleThreadActivity.mPosts) {
            Matcher m = p.matcher(post.getComment());
            if (m.find()) {
                //postAnswers = new ArrayList<>();
                Log.d(LOG_TAG, "groupCount: " + m.groupCount());
                String answer;
                String rawAnswer;
                for (int i = 0; i < m.groupCount(); i++) {
                    rawAnswer = m.group(i);
                    Log.d(LOG_TAG, "rawAnswer: " + rawAnswer);
                    if (rawAnswer.contains("http")) {
                        continue;
                    }
                    answer = rawAnswer.substring(rawAnswer.indexOf('#') + 1, rawAnswer.length() - 1);
                    if (mAnswers.get(answer) == null) {
                        postAnswers = new ArrayList<>();
                        postAnswers.add(post.getNum());
                        mAnswers.put(answer, postAnswers);
                    } else {
                        postAnswers = mAnswers.get(answer);
                        postAnswers.add(post.getNum());
                        mAnswers.put(answer, postAnswers);
                    }
                }
                //mAnswers.put(SingleThreadActivity.mPosts.indexOf(post), postAnswers);

            } else {
                //Log.d(LOG_TAG, "nothing found");
            }


        }
    }

    private void setupLocationsList() {
        mAnswersSpansLocations = new ArrayList<>();
        for (int i = 0; i < getItemCount(); i++) {
            mAnswersSpansLocations.add(i, new ArrayList<Integer>());
        }
    }

    private SpannableString setAnswersBackgroundSpansIfNeeded(
            SpannableString spannableAnswersString, int position) {
        List<Integer> spansLocations = mAnswersSpansLocations.get(position);
        Log.d(LOG_TAG, "spansLocations: " + spansLocations);
        if (spansLocations.size() != 0) {
            spannableAnswersString.setSpan(foregroundColorSpan,
                    spansLocations.get(0), spansLocations.get(1),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannableAnswersString;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Post post = SingleThreadActivity.mPosts.get(position);

        final String number = post.getNum();
        String time = post.getDate();
        String name = post.getName();
        String trip = post.getTrip();
        String subject = post.getSubject();
        String comment = post.getComment();
        List<Files> files = post.getFiles();

        SpannableStringBuilder numberAndTimeString = new SpannableStringBuilder("#" + (position + 1) + " ");
        numberAndTimeString.setSpan(new ForegroundColorSpan(
                mActivity.getResources().getColor(R.color.post_number_color)),
                0, numberAndTimeString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        numberAndTimeString.append(Html.fromHtml("№" + number
                + (name.equals("") ? "" : " " + name) + " "
                + (trip.equals("") ? "" : " " + trip) + time));
        holder.numberAndTime.setText(numberAndTimeString);

        if (!boardId.equals("b")) {
            holder.subject.setText(Html.fromHtml(subject));
        } else {
            holder.subject.setVisibility(View.GONE);
        }
        if (subject.equals("")) holder.subject.setVisibility(View.GONE);

        holder.comment.setText(Html.fromHtml(comment));
        holder.comment.setMovementMethod(CommentLinkMovementMethod.getInstance());

        if (position == 0) {
            Log.d(LOG_TAG, "mAnswers for 0 position: " + mAnswers.get(number));
        }


        if (mAnswers.containsKey(number)) {
            holder.answers.setVisibility(View.VISIBLE);
            SpannableStringBuilder answerStringBuilder
                    = new SpannableStringBuilder(mActivity.getString(R.string.answers_text));
            answerStringBuilder.setSpan(new StyleSpan(android.graphics.Typeface.ITALIC),
                    0, answerStringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            answerStringBuilder.append(" ");
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
                answerStringBuilder.append(", ");
            }

            SpannableString spannableAnswersString = SpannableString.valueOf(
                    answerStringBuilder.delete(answerStringBuilder.length() - 2, answerStringBuilder.length()));
            spannableAnswersString = setAnswersBackgroundSpansIfNeeded(spannableAnswersString, position);

            holder.answers.setText(spannableAnswersString);
            holder.answers.setMovementMethod(AnswersLinkMovementMethod.getInstance(mActivity, position));
        } else {
            Log.d(LOG_TAG, "no answers for position " + position);
            holder.answers.setVisibility(View.GONE);
        }
        if (position == 0) {
            if (subject.equals("")) {
                ((TextView)mActivity.toolbar.findViewById(R.id.title)).setText(comment);
            } else {
                ((TextView)mActivity.toolbar.findViewById(R.id.title)).setText(subject);
            }
        }

        String width;
        String height;
        String thumbnail;
        String size;

        int filesSize = files.size();
        switch (filesSize) {
            case 1: {
                if (holder.imageAndSummaryContainer1 != null)
                    holder.imageAndSummaryContainer1.setVisibility(View.VISIBLE);
                if (holder.imageAndSummaryContainer2 != null)
                    holder.imageAndSummaryContainer2.setVisibility(View.GONE);
                if (holder.imageAndSummaryContainer3 != null)
                    holder.imageAndSummaryContainer3.setVisibility(View.GONE);
                if (holder.imageAndSummaryContainer4 != null)
                    holder.imageAndSummaryContainer4.setVisibility(View.GONE);
                break;
            }
            case 2: {
                if (holder.imageAndSummaryContainer1 != null)
                    holder.imageAndSummaryContainer1.setVisibility(View.VISIBLE);
                if (holder.imageAndSummaryContainer2 != null)
                    holder.imageAndSummaryContainer2.setVisibility(View.VISIBLE);
                if (holder.imageAndSummaryContainer3 != null)
                    holder.imageAndSummaryContainer3.setVisibility(View.GONE);
                if (holder.imageAndSummaryContainer4 != null)
                    holder.imageAndSummaryContainer4.setVisibility(View.GONE);
                break;
            }
            case 3: {
                if (holder.imageAndSummaryContainer1 != null)
                    holder.imageAndSummaryContainer1.setVisibility(View.VISIBLE);
                if (holder.imageAndSummaryContainer2 != null)
                    holder.imageAndSummaryContainer2.setVisibility(View.VISIBLE);
                if (holder.imageAndSummaryContainer3 != null)
                    holder.imageAndSummaryContainer3.setVisibility(View.VISIBLE);
                if (holder.imageAndSummaryContainer4 != null)
                    holder.imageAndSummaryContainer4.setVisibility(View.GONE);
                break;
            }
            case 4: {
                if (holder.imageAndSummaryContainer1 != null)
                    holder.imageAndSummaryContainer1.setVisibility(View.VISIBLE);
                if (holder.imageAndSummaryContainer2 != null)
                    holder.imageAndSummaryContainer2.setVisibility(View.VISIBLE);
                if (holder.imageAndSummaryContainer3 != null)
                    holder.imageAndSummaryContainer3.setVisibility(View.VISIBLE);
                if (holder.imageAndSummaryContainer4 != null)
                    holder.imageAndSummaryContainer4.setVisibility(View.VISIBLE);
                break;
            }
        }

        if (files.size() != 0) {
            for (int i = 0; i < files.size(); i++) {
                Files file = files.get(i);

                width = file.getWidth();
                height = file.getHeight();
                thumbnail = file.getThumbnail();
                size = file.getSize();

                if (file.getPath().substring(file.getPath().length() - 3, file.getPath().length()).equals("gif")) {
                    ornutVGolosinu();
                }

                if (files.size() == 1) {

                    mActivity.imageLoader.clearMemoryCache();

                    mActivity.imageLoader.displayImage(Constants.DVACH_BASE_URL + thumbnail, holder.image);
                    setImageViewWidthDependingOnOrientation(mActivity.getResources().getConfiguration(), holder.image);
                    //setImageViewContainerWidthDependingOnOrientation(mActivity.getResources().getConfiguration(), holder.imageAndSummaryContainer);

                    if (file.getPath().substring(file.getPath().length() - 4, file.getPath().length()).equals(Formats.WEBM)) {
                        holder.webmImageView.setVisibility(View.VISIBLE);
                        holder.webmImageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showFullPicVid(SingleThreadActivity.mPosts.get(position).getFiles().get(0));
                            }
                        });
                    } else holder.webmImageView.setVisibility(View.GONE);
                    holder.image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showFullPicVid(SingleThreadActivity.mPosts.get(position).getFiles().get(0));
                        }
                    });

                    holder.summary.setText(size + "Кб, " + width + "x" + height);
                } else if (files.size() <= 4) {
                    switch (i) {
                        case 0: {
                            mActivity.imageLoader.clearMemoryCache();
                            mActivity.imageLoader.displayImage(Constants.DVACH_BASE_URL + thumbnail, holder.image1);
                            setImageViewWidthDependingOnOrientation(mActivity.getResources().getConfiguration(), holder.image1);
                            //setImageViewContainerWidthDependingOnOrientation(mActivity.getResources().getConfiguration(), holder.imageAndSummaryContainer1);
                            if (file.getPath().substring(file.getPath().length() - 4, file.getPath().length()).equals(Formats.WEBM)) {
                                holder.webmImageView1.setVisibility(View.VISIBLE);
                                holder.webmImageView1.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        showFullPicVid(SingleThreadActivity.mPosts.get(position).getFiles().get(0));
                                    }
                                });
                            } else holder.webmImageView1.setVisibility(View.GONE);
                            holder.image1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    showFullPicVid(SingleThreadActivity.mPosts.get(position).getFiles().get(0));
                                }
                            });
                            holder.summary1.setText(size + "Кб, " + width + "x" + height);
                            break;
                        }
                        case 1: {
                            mActivity.imageLoader.clearMemoryCache();
                            mActivity.imageLoader.displayImage(Constants.DVACH_BASE_URL + thumbnail, holder.image2);
                            setImageViewWidthDependingOnOrientation(mActivity.getResources().getConfiguration(), holder.image2);
                            //setImageViewContainerWidthDependingOnOrientation(mActivity.getResources().getConfiguration(), holder.imageAndSummaryContainer2);
                            if (file.getPath().substring(file.getPath().length() - 4, file.getPath().length()).equals(Formats.WEBM)) {
                                holder.webmImageView2.setVisibility(View.VISIBLE);
                                holder.webmImageView2.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        showFullPicVid(SingleThreadActivity.mPosts.get(position).getFiles().get(1));
                                    }
                                });
                            } else holder.webmImageView2.setVisibility(View.GONE);
                            holder.image2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    showFullPicVid(SingleThreadActivity.mPosts.get(position).getFiles().get(1));
                                }
                            });
                            holder.summary2.setText(size + "Кб, " + width + "x" + height);
                            break;
                        }
                        case 2: {
                            mActivity.imageLoader.clearMemoryCache();
                            mActivity.imageLoader.displayImage(Constants.DVACH_BASE_URL + thumbnail, holder.image3);
                            setImageViewWidthDependingOnOrientation(mActivity.getResources().getConfiguration(), holder.image3);
                            //setImageViewContainerWidthDependingOnOrientation(mActivity.getResources().getConfiguration(), holder.imageAndSummaryContainer3);
                            if (file.getPath().substring(file.getPath().length() - 4, file.getPath().length()).equals(Formats.WEBM)) {
                                holder.webmImageView3.setVisibility(View.VISIBLE);
                                holder.webmImageView3.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        showFullPicVid(SingleThreadActivity.mPosts.get(position).getFiles().get(2));
                                    }
                                });
                            } else holder.webmImageView3.setVisibility(View.GONE);
                            holder.image3.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    showFullPicVid(SingleThreadActivity.mPosts.get(position).getFiles().get(2));
                                }
                            });
                            holder.summary3.setText(size + "Кб, " + width + "x" + height);
                            break;
                        }
                        case 3: {
                            mActivity.imageLoader.clearMemoryCache();
                            mActivity.imageLoader.displayImage(Constants.DVACH_BASE_URL + thumbnail, holder.image4);
                            //setImageViewContainerWidthDependingOnOrientation(mActivity.getResources().getConfiguration(), holder.imageAndSummaryContainer4);
                            setImageViewWidthDependingOnOrientation(mActivity.getResources().getConfiguration(), holder.image4);
                            if (file.getPath().substring(file.getPath().length() - 4, file.getPath().length()).equals(Formats.WEBM)) {
                                holder.webmImageView4.setVisibility(View.VISIBLE);
                                holder.webmImageView4.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        showFullPicVid(SingleThreadActivity.mPosts.get(position).getFiles().get(3));
                                    }
                                });
                            } else holder.webmImageView4.setVisibility(View.GONE);
                            holder.image4.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    showFullPicVid(SingleThreadActivity.mPosts.get(position).getFiles().get(3));
                                }
                            });
                            holder.summary4.setText(size + "Кб, " + width + "x" + height);
                            break;
                        }
                    }
                }
            }
        }

    }

    public View getViewForPosition(final int position) {
        View rootView = inflateProperView(getItemViewType(position));
        TextView numberAndTimeTextView = (TextView) rootView.findViewById(R.id.post_number_and_time_info);
        TextView subjectTextView = (TextView) rootView.findViewById(R.id.post_subject);
        TextView commentTextView = (TextView) rootView.findViewById(R.id.post_comment);
        TextView answersTextView = (TextView) rootView.findViewById(R.id.answers);
        LinearLayout imageAndSummaryContainer1 = null;
        LinearLayout imageAndSummaryContainer2 = null;
        LinearLayout imageAndSummaryContainer3 = null;
        LinearLayout imageAndSummaryContainer4 = null;
        ImageView image = null;
        ImageView image1 = null;
        ImageView image2 = null;
        ImageView image3 = null;
        ImageView image4 = null;
        ImageView webmImageView = null;
        ImageView webmImageView1 = null;
        ImageView webmImageView2 = null;
        ImageView webmImageView3 = null;
        ImageView webmImageView4 = null;
        TextView summary = null;
        TextView summary1 = null;
        TextView summary2 = null;
        TextView summary3 = null;
        TextView summary4 = null;
        if (getItemViewType(position) == Constants.ITEM_SINGLE_IMAGE) {
            image = (ImageView) rootView.findViewById(R.id.post_image);
            webmImageView = (ImageView) rootView.findViewById(R.id.webm_imageview);
            summary = (TextView) rootView.findViewById(R.id.image_summary);
        }
        if (getItemViewType(position) == Constants.ITEM_MULTIPLE_IMAGES) {
            imageAndSummaryContainer1 = (LinearLayout) rootView.findViewById(R.id.image_with_summary_container_1);
            imageAndSummaryContainer2 = (LinearLayout) rootView.findViewById(R.id.image_with_summary_container_2);
            imageAndSummaryContainer3 = (LinearLayout) rootView.findViewById(R.id.image_with_summary_container_3);
            imageAndSummaryContainer4 = (LinearLayout) rootView.findViewById(R.id.image_with_summary_container_4);
            image1 = (ImageView) rootView.findViewById(R.id.post_image_1);
            webmImageView1 = (ImageView) rootView.findViewById(R.id.webm_imageview_1);
            image2 = (ImageView) rootView.findViewById(R.id.post_image_2);
            webmImageView2 = (ImageView) rootView.findViewById(R.id.webm_imageview_2);
            image3 = (ImageView) rootView.findViewById(R.id.post_image_3);
            webmImageView3 = (ImageView) rootView.findViewById(R.id.webm_imageview_3);
            image4 = (ImageView) rootView.findViewById(R.id.post_image_4);
            webmImageView4 = (ImageView) rootView.findViewById(R.id.webm_imageview_4);
            summary1 = (TextView) rootView.findViewById(R.id.image_summary_1);
            summary2 = (TextView) rootView.findViewById(R.id.image_summary_2);
            summary3 = (TextView) rootView.findViewById(R.id.image_summary_3);
            summary4 = (TextView) rootView.findViewById(R.id.image_summary_4);
        }



        Post post = SingleThreadActivity.mPosts.get(position);

        final String number = post.getNum();
        String time = post.getDate();
        String name = post.getName();
        String trip = post.getTrip();
        String subject = post.getSubject();
        String comment = post.getComment();
        List<Files> files = post.getFiles();

        SpannableStringBuilder numberAndTimeString = new SpannableStringBuilder("#" + (position + 1) + " ");
        numberAndTimeString.setSpan(new ForegroundColorSpan(
                        mActivity.getResources().getColor(R.color.post_number_color)),
                0, numberAndTimeString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        numberAndTimeString.append(Html.fromHtml("№" + number
                + (name.equals("") ? "" : " " + name) + " "
                + (trip.equals("") ? "" : " " + trip) + time));
        numberAndTimeTextView.setText(numberAndTimeString);

        if (!boardId.equals("b")) {
            subjectTextView.setText(Html.fromHtml(subject));
        } else {
            subjectTextView.setVisibility(View.GONE);
        }
        if (subject.equals("")) subjectTextView.setVisibility(View.GONE);

        commentTextView.setText(Html.fromHtml(comment));
        commentTextView.setMovementMethod(CommentLinkMovementMethod.getInstance());

        if (position == 0) {
            Log.d(LOG_TAG, "mAnswers for 0 position: " + mAnswers.get(number));
        }


        if (mAnswers.containsKey(number)) {
            answersTextView.setVisibility(View.VISIBLE);
            SpannableStringBuilder answerStringBuilder
                    = new SpannableStringBuilder(mActivity.getString(R.string.answers_text));
            answerStringBuilder.setSpan(new StyleSpan(android.graphics.Typeface.ITALIC),
                    0, answerStringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            answerStringBuilder.append(" ");
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
                answerStringBuilder.append(", ");
            }

            SpannableString spannableAnswersString = SpannableString.valueOf(
                    answerStringBuilder.delete(answerStringBuilder.length() - 2, answerStringBuilder.length()));
            spannableAnswersString = setAnswersBackgroundSpansIfNeeded(spannableAnswersString, position);

            answersTextView.setText(spannableAnswersString);
            answersTextView.setMovementMethod(AnswersLinkMovementMethod.getInstance(mActivity, position));
        } else {
            Log.d(LOG_TAG, "no answers for position " + position);
            answersTextView.setVisibility(View.GONE);
        }
        if (position == 0) {
            if (subject.equals("")) {
                ((TextView)mActivity.toolbar.findViewById(R.id.title)).setText(comment);
            } else {
                ((TextView)mActivity.toolbar.findViewById(R.id.title)).setText(subject);
            }
        }

        String width;
        String height;
        String thumbnail;
        String size;

        int filesSize = files.size();
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
                break;
            }
        }

        if (files.size() != 0) {
            for (int i = 0; i < files.size(); i++) {
                Files file = files.get(i);

                width = file.getWidth();
                height = file.getHeight();
                thumbnail = file.getThumbnail();
                size = file.getSize();

                if (file.getPath().substring(file.getPath().length() - 3, file.getPath().length()).equals("gif")) {
                    ornutVGolosinu();
                }

                if (files.size() == 1) {

                    mActivity.imageLoader.clearMemoryCache();

                    mActivity.imageLoader.displayImage(Constants.DVACH_BASE_URL + thumbnail, image);
                    setImageViewWidthDependingOnOrientation(mActivity.getResources().getConfiguration(), image);
                    //setImageViewContainerWidthDependingOnOrientation(mActivity.getResources().getConfiguration(), holder.imageAndSummaryContainer);

                    if (file.getPath().substring(file.getPath().length() - 4, file.getPath().length()).equals(Formats.WEBM)) {
                        webmImageView.setVisibility(View.VISIBLE);
                        webmImageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showFullPicVid(SingleThreadActivity.mPosts.get(position).getFiles().get(0));
                            }
                        });
                    } else webmImageView.setVisibility(View.GONE);
                    image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            showFullPicVid(SingleThreadActivity.mPosts.get(position).getFiles().get(0));
                        }
                    });

                    summary.setText(size + "Кб, " + width + "x" + height);
                } else if (files.size() <= 4) {
                    switch (i) {
                        case 0: {
                            mActivity.imageLoader.clearMemoryCache();
                            mActivity.imageLoader.displayImage(Constants.DVACH_BASE_URL + thumbnail, image1);
                            setImageViewWidthDependingOnOrientation(mActivity.getResources().getConfiguration(), image1);
                            //setImageViewContainerWidthDependingOnOrientation(mActivity.getResources().getConfiguration(), holder.imageAndSummaryContainer1);
                            if (file.getPath().substring(file.getPath().length() - 4, file.getPath().length()).equals(Formats.WEBM)) {
                                webmImageView1.setVisibility(View.VISIBLE);
                                webmImageView1.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        showFullPicVid(SingleThreadActivity.mPosts.get(position).getFiles().get(0));
                                    }
                                });
                            } else webmImageView1.setVisibility(View.GONE);
                            image1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    showFullPicVid(SingleThreadActivity.mPosts.get(position).getFiles().get(0));
                                }
                            });
                            summary1.setText(size + "Кб, " + width + "x" + height);
                            break;
                        }
                        case 1: {
                            mActivity.imageLoader.clearMemoryCache();
                            mActivity.imageLoader.displayImage(Constants.DVACH_BASE_URL + thumbnail, image2);
                            setImageViewWidthDependingOnOrientation(mActivity.getResources().getConfiguration(), image2);
                            //setImageViewContainerWidthDependingOnOrientation(mActivity.getResources().getConfiguration(), holder.imageAndSummaryContainer2);
                            if (file.getPath().substring(file.getPath().length() - 4, file.getPath().length()).equals(Formats.WEBM)) {
                                webmImageView2.setVisibility(View.VISIBLE);
                                webmImageView2.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        showFullPicVid(SingleThreadActivity.mPosts.get(position).getFiles().get(1));
                                    }
                                });
                            } else webmImageView2.setVisibility(View.GONE);
                            image2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    showFullPicVid(SingleThreadActivity.mPosts.get(position).getFiles().get(1));
                                }
                            });
                            summary2.setText(size + "Кб, " + width + "x" + height);
                            break;
                        }
                        case 2: {
                            mActivity.imageLoader.clearMemoryCache();
                            mActivity.imageLoader.displayImage(Constants.DVACH_BASE_URL + thumbnail, image3);
                            setImageViewWidthDependingOnOrientation(mActivity.getResources().getConfiguration(), image3);
                            //setImageViewContainerWidthDependingOnOrientation(mActivity.getResources().getConfiguration(), holder.imageAndSummaryContainer3);
                            if (file.getPath().substring(file.getPath().length() - 4, file.getPath().length()).equals(Formats.WEBM)) {
                                webmImageView3.setVisibility(View.VISIBLE);
                                webmImageView3.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        showFullPicVid(SingleThreadActivity.mPosts.get(position).getFiles().get(2));
                                    }
                                });
                            } else webmImageView3.setVisibility(View.GONE);
                            image3.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    showFullPicVid(SingleThreadActivity.mPosts.get(position).getFiles().get(2));
                                }
                            });
                            summary3.setText(size + "Кб, " + width + "x" + height);
                            break;
                        }
                        case 3: {
                            mActivity.imageLoader.clearMemoryCache();
                            mActivity.imageLoader.displayImage(Constants.DVACH_BASE_URL + thumbnail, image4);
                            //setImageViewContainerWidthDependingOnOrientation(mActivity.getResources().getConfiguration(), holder.imageAndSummaryContainer4);
                            setImageViewWidthDependingOnOrientation(mActivity.getResources().getConfiguration(), image4);
                            if (file.getPath().substring(file.getPath().length() - 4, file.getPath().length()).equals(Formats.WEBM)) {
                                webmImageView4.setVisibility(View.VISIBLE);
                                webmImageView4.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        showFullPicVid(SingleThreadActivity.mPosts.get(position).getFiles().get(3));
                                    }
                                });
                            } else webmImageView4.setVisibility(View.GONE);
                            image4.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    showFullPicVid(SingleThreadActivity.mPosts.get(position).getFiles().get(3));
                                }
                            });
                            summary4.setText(size + "Кб, " + width + "x" + height);
                            break;
                        }
                    }
                }
            }
        }

        return rootView;

    }

    @SuppressLint("UseSparseArrays")
    private void showFullPicVid(Files file) {
        if (DeviceUtils.getApiInt() >= 19) UIUtils.showSystemUI(mActivity);
        if (DeviceUtils.getApiInt() >= 19) UIUtils.setBarsTranslucent(mActivity, true);

        mActivity.fullPicVidOpened = true;

        Log.d(LOG_TAG, "appbar offset: " + mActivity.appBarVerticalOffSet);
        Log.d(LOG_TAG, "appbar height: " + mActivity.appBarLayout.getHeight());

        SingleThreadActivity.imageCachePaths = new ArrayList<>();
        SingleThreadActivity.galleryFragments = new HashMap<>();

        UIUtils.barsAreShown = true;

        SingleThreadActivity.files = new ArrayList<>();
        for (Post post : SingleThreadActivity.mPosts) {
            SingleThreadActivity.files.addAll(post.getFiles());
        }
        int currentPosition = SingleThreadActivity.files.indexOf(file);

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
            mActivity.picVidToolbarTitleTextView.setText("noname.hz");
        } else {
            mActivity.picVidToolbarTitleTextView.setText(displayName);

        }
        mActivity.picVidToolbarTitleTextView.setTypeface(Typeface.DEFAULT_BOLD);
        mActivity.picVidToolbarTitleTextView.setTextSize(16.0f);
        mActivity.picVidToolbarShortInfoTextView.setText("(" + (currentPosition + 1) + "/"
                + SingleThreadActivity.files.size() + "), " + SingleThreadActivity.files.get(currentPosition).getWidth() + "x"
                + SingleThreadActivity.files.get(currentPosition).getHeight() + ", "
                + SingleThreadActivity.files.get(currentPosition).getSize() + " кб");
    }

    private void setImageViewWidthDependingOnOrientation(Configuration configuration, ImageView imageView) {
        //Log.d(LOG_TAG, "setImageViewWidthDependingOnOrientation:");
        if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            imageView.getLayoutParams().width = DeviceUtils.apiIs20OrHigher() ? 160 : 80;
            imageView.requestLayout();
        } else {
            //Log.d(LOG_TAG, "landscape width");
            imageView.getLayoutParams().width = DeviceUtils.apiIs20OrHigher() ? 200 : 100;
            imageView.requestLayout();
        }
    }

    public void notifyNewPosts(int before, int after) {
        String toShow = StringUtils.correctNotifyNewPostsString(after - before);
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
      return SingleThreadActivity.mPosts.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (SingleThreadActivity.mPosts.get(position).getFiles().size() == 0) {
            return Constants.ITEM_NO_IMAGES;
        } else if (SingleThreadActivity.mPosts.get(position).getFiles().size() == 1) {
            return Constants.ITEM_SINGLE_IMAGE;
        } else if (SingleThreadActivity.mPosts.get(position).getFiles().size() > 1) {
            return Constants.ITEM_MULTIPLE_IMAGES;
        }
        return -1;
    }
}
