package com.koresuniku.wishmaster.adapters;

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
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.koresuniku.wishmaster.R;
import com.koresuniku.wishmaster.activities.SingleThreadActivity;
import com.koresuniku.wishmaster.http.single_thread_api.models.Post;
import com.koresuniku.wishmaster.http.threads_api.models.Files;
import com.koresuniku.wishmaster.ui.UiUtils;
import com.koresuniku.wishmaster.ui.text.AnswersLinkMovementMethod;
import com.koresuniku.wishmaster.ui.text.CommentLinkMovementMethod;
import com.koresuniku.wishmaster.ui.views.NoScrollTextView;
import com.koresuniku.wishmaster.utils.Constants;
import com.koresuniku.wishmaster.utils.DeviceUtils;
import com.koresuniku.wishmaster.utils.Formats;
import com.koresuniku.wishmaster.utils.HtmlUtils;
import com.koresuniku.wishmaster.utils.StringUtils;
import com.koresuniku.wishmaster.utils.listeners.SingleThreadViewPagerOnPageChangeListener;

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
        mAnswers = new HashMap<>();
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

    private void getAnswersForPost() {
        HtmlUtils.getAnswersForPost(mActivity);
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
//        List<Integer> spansAnswers = mAnswersSpansLocations.get(position);
//        Log.d(LOG_TAG, "spansLocations: " + spansAnswers);
//        if (spansAnswers.size() != 0) {
//            spannableAnswersString.setSpan(foregroundColorSpan,
//                    spansAnswers.get(0), spansAnswers.get(1),
//                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        }
        return spannableAnswersString;
    }

    private SpannableString setCommentAnswersBackgroundSpansIfNeeded(
            SpannableString spannableAnswersString, int position) {
//        List<Integer> spansCommentLocations = mCommentAnswersSpansLocations.get(position);
//        if (spansCommentLocations.size() != 0) {
//            spannableAnswersString.setSpan(foregroundColorSpan,
//                    spansCommentLocations.get(0), spansCommentLocations.get(1),
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

    private Spannable getCorrectSpannableForCommentTextView(String comment, int position) {
        SpannableString spannableCommentAnswerString = new SpannableString(Html.fromHtml(comment));
        return setCommentAnswersBackgroundSpansIfNeeded(spannableCommentAnswerString, position);
    }

    private Spannable getCorrectSpannableForAnswersTextView(TextView answers, String number, int position) {
        if (mAnswers.containsKey(number)) {
            answers.setVisibility(View.VISIBLE);
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
            return spannableAnswersString;

        } else answers.setVisibility(View.GONE); return new SpannableString("");
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Post post = mActivity.mPosts.get(position);

        final String number = post.getNum();
        String time = post.getDate();
        String name = post.getName();
        String trip = post.getTrip();
        String subject = post.getSubject();
        String comment = post.getComment();
        List<Files> files = post.getFiles();

        SpannableStringBuilder numberAndTimeString = new SpannableStringBuilder(
                "#" + (position + 1) + " ");
        numberAndTimeString.setSpan(new ForegroundColorSpan(
                mActivity.getResources().getColor(R.color.post_number_color)),
                0, numberAndTimeString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        numberAndTimeString.append(
                Html.fromHtml(StringUtils.getNumberAndTimeString(number, name, trip, time)));
        holder.numberAndTime.setText(numberAndTimeString);
        if (!boardId.equals("b")) holder.subject.setText(Html.fromHtml(subject));
        else holder.subject.setVisibility(View.GONE);
        if (subject.equals("")) holder.subject.setVisibility(View.GONE);
        holder.comment.setText(getCorrectSpannableForCommentTextView(comment, position));
        holder.comment.setMovementMethod(CommentLinkMovementMethod.getInstance(mActivity, position));
        holder.answers.setText(getCorrectSpannableForAnswersTextView(holder.answers, number, position));
        holder.answers.setMovementMethod(AnswersLinkMovementMethod.getInstance(mActivity, position));
        if (position == 0) {
            if (subject.equals("")) ((TextView)mActivity.toolbar.findViewById(R.id.title)).setText(comment);
            else ((TextView)mActivity.toolbar.findViewById(R.id.title)).setText(subject);
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
            } else if (files.size() <= 4) {
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
                }
            }
        }
    }

    private void switchImagesVisibility(SingleThreadRecyclerViewAdapter.ViewHolder holder, int filesSize) {
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
    }

    private void switchImagesVisibility(RelativeLayout imageAndSummaryContainer1,
                                        RelativeLayout imageAndSummaryContainer2,
                                        RelativeLayout imageAndSummaryContainer3,
                                        RelativeLayout imageAndSummaryContainer4,
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
        summary.setText(StringUtils.getSummaryString(mActivity, imageOrVideoSize,
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
                        image.startAnimation(com.koresuniku.wishmaster.utils.AnimationUtils.resizeThumbnail(
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
            image = (ImageView) singleView.findViewById(R.id.post_image);
            webmImageView = (ImageView) singleView.findViewById(R.id.webm_imageview);
            summary = (TextView) singleView.findViewById(R.id.image_summary);
        }
        if (getItemViewType(position) == Constants.ITEM_MULTIPLE_IMAGES) {
            imageAndSummaryContainer1 = (RelativeLayout) singleView.findViewById(R.id.image_with_summary_container_1);
            imageAndSummaryContainer2 = (RelativeLayout) singleView.findViewById(R.id.image_with_summary_container_2);
            imageAndSummaryContainer3 = (RelativeLayout) singleView.findViewById(R.id.image_with_summary_container_3);
            imageAndSummaryContainer4 = (RelativeLayout) singleView.findViewById(R.id.image_with_summary_container_4);
            image1 = (ImageView) singleView.findViewById(R.id.post_image_1);
            webmImageView1 = (ImageView) singleView.findViewById(R.id.webm_imageview_1);
            image2 = (ImageView) singleView.findViewById(R.id.post_image_2);
            webmImageView2 = (ImageView) singleView.findViewById(R.id.webm_imageview_2);
            image3 = (ImageView) singleView.findViewById(R.id.post_image_3);
            webmImageView3 = (ImageView) singleView.findViewById(R.id.webm_imageview_3);
            image4 = (ImageView) singleView.findViewById(R.id.post_image_4);
            webmImageView4 = (ImageView) singleView.findViewById(R.id.webm_imageview_4);
            summary1 = (TextView) singleView.findViewById(R.id.image_summary_1);
            summary2 = (TextView) singleView.findViewById(R.id.image_summary_2);
            summary3 = (TextView) singleView.findViewById(R.id.image_summary_3);
            summary4 = (TextView) singleView.findViewById(R.id.image_summary_4);
        }
        Post post = mActivity.mPosts.get(position);

        final String number = post.getNum();
        String time = post.getDate();
        String name = post.getName();
        String trip = post.getTrip();
        String subject = post.getSubject();
        String comment = post.getComment();
        List<Files> files = post.getFiles();

        SpannableStringBuilder numberAndTimeString = new SpannableStringBuilder(
                "#" + (position + 1) + " ");
        numberAndTimeString.setSpan(new ForegroundColorSpan(
                        mActivity.getResources().getColor(R.color.post_number_color)),
                0, numberAndTimeString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        numberAndTimeString.append(
                Html.fromHtml(StringUtils.getNumberAndTimeString(number, name, trip, time)));
        numberAndTimeTextView.setText(numberAndTimeString);
        if (!boardId.equals("b")) subjectTextView.setText(Html.fromHtml(subject));
        else subjectTextView.setVisibility(View.GONE);
        if (subject.equals(""))subjectTextView.setVisibility(View.GONE);
        commentTextView.setText(getCorrectSpannableForCommentTextView(comment, position));
        commentTextView.setMovementMethod(CommentLinkMovementMethod.getInstance(mActivity, position));
        commentTextView.setText(getCorrectSpannableForAnswersTextView(answersTextView, number, position));
        answersTextView.setMovementMethod(AnswersLinkMovementMethod.getInstance(mActivity, position));
        if (position == 0) {
            if (subject.equals("")) ((TextView)mActivity.toolbar.findViewById(R.id.title)).setText(comment);
            else ((TextView)mActivity.toolbar.findViewById(R.id.title)).setText(subject);
        }

        String width;
        String height;
        String thumbnail;
        String size;

        int filesSize = files.size();
        switchImagesVisibility(imageAndSummaryContainer1, imageAndSummaryContainer2,
                imageAndSummaryContainer3, imageAndSummaryContainer4, filesSize);

        for (int i = 0; i < filesSize; i++) {
            Files file = files.get(i);

            width = file.getWidth();
            height = file.getHeight();
            thumbnail = file.getThumbnail();
            size = file.getSize();

            if (files.size() == 1) {
                setupImageContainer(image,webmImageView, summary, thumbnail, file, size, width, height);
            } else if (files.size() <= 4) {
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
            mActivity.picVidToolbarTitleTextView.setText(mActivity.getString(R.string.noname_text));
        } else {
            mActivity.picVidToolbarTitleTextView.setText(displayName);
        }
        mActivity.picVidToolbarTitleTextView.setTypeface(Typeface.DEFAULT_BOLD);
        mActivity.picVidToolbarTitleTextView.setTextSize(16.0f);
        mActivity.picVidToolbarShortInfoTextView.setText(StringUtils.getShortInfoForToolbarString(
                mActivity.picVidToolbarShortInfoTextView, currentPosition, SingleThreadActivity.files));
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
        String toShow = StringUtils.getCorrectNotifyNewPostsString(after - before);
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
