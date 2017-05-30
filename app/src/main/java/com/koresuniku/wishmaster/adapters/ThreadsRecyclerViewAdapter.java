package com.koresuniku.wishmaster.adapters;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.koresuniku.wishmaster.R;
import com.koresuniku.wishmaster.activities.SingleThreadActivity;
import com.koresuniku.wishmaster.activities.ThreadsActivity;
import com.koresuniku.wishmaster.http.threads_api.models.Files;
import com.koresuniku.wishmaster.http.threads_api.models.Thread;
import com.koresuniku.wishmaster.utils.DeviceUtils;
import com.koresuniku.wishmaster.utils.Constants;
import com.koresuniku.wishmaster.utils.Formats;
import com.koresuniku.wishmaster.ui.UiUtils;
import com.koresuniku.wishmaster.utils.StringUtils;
import com.koresuniku.wishmaster.utils.listeners.ThreadsViewPagerOnPageChangeListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ThreadsRecyclerViewAdapter extends RecyclerView.Adapter<ThreadsRecyclerViewAdapter.ViewHolder> {
    private final String LOG_TAG = ThreadsRecyclerViewAdapter.class.getSimpleName();

    private ThreadsRecyclerViewAdapter thisAdapter = this;
    private ThreadsActivity mActivity;
    private String boardId;
    private int mViewType;
    private Bitmap webmBitmap;

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
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
        private TextView postsAndFiles;
        private View indicatorView;
        private TextView indicatorTextView;


        public ViewHolder(View itemView) {
            super(itemView);

            threadItemContainer = (RelativeLayout) itemView.findViewById(R.id.thread_item_container);
            numberAndTime = (TextView) itemView.findViewById(R.id.thread_number_and_time_info);
            subject = (TextView) itemView.findViewById(R.id.thread_subject);
            if (mViewType == Constants.ITEM_SINGLE_IMAGE) {
                image = (ImageView) itemView.findViewById(R.id.thread_image);
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
                image1 = (ImageView) itemView.findViewById(R.id.thread_image_1);
                webmImageView1 = (ImageView) itemView.findViewById(R.id.webm_imageview_1);
                image2 = (ImageView) itemView.findViewById(R.id.thread_image_2);
                webmImageView2 = (ImageView) itemView.findViewById(R.id.webm_imageview_2);
                image3 = (ImageView) itemView.findViewById(R.id.thread_image_3);
                webmImageView3 = (ImageView) itemView.findViewById(R.id.webm_imageview_3);
                image4 = (ImageView) itemView.findViewById(R.id.thread_image_4);
                webmImageView4 = (ImageView) itemView.findViewById(R.id.webm_imageview_4);
                summary1 = (TextView) itemView.findViewById(R.id.image_summary_1);
                summary2 = (TextView) itemView.findViewById(R.id.image_summary_2);
                summary3 = (TextView) itemView.findViewById(R.id.image_summary_3);
                summary4 = (TextView) itemView.findViewById(R.id.image_summary_4);
            }
            comment = (TextView) itemView.findViewById(R.id.thread_comment);
            postsAndFiles = (TextView) itemView.findViewById(R.id.posts_and_files_info);
            indicatorView = itemView.findViewById(R.id.threads_page_indicator_view);
            indicatorTextView = (TextView) itemView.findViewById(R.id.page_indicator_textview);

            setIsRecyclable(true);
        }
    }

    public ThreadsRecyclerViewAdapter(ThreadsActivity activity,String board) {
        mActivity = activity;
        boardId = board;
        webmBitmap = BitmapFactory.decodeResource(mActivity.getResources(), R.drawable.webm);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case Constants.ITEM_NO_IMAGES: {
                mViewType = viewType;
                view = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.thread_item_no_images, parent, false);
                return new ViewHolder(view);
            }
            case Constants.ITEM_SINGLE_IMAGE: {
                mViewType = viewType;
                view = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.thread_item_single_image_redesign, parent, false);
                return new ViewHolder(view);
            }
            case Constants.ITEM_MULTIPLE_IMAGES: {
                mViewType = viewType;
                view = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.thread_item_multiple_images_redesign, parent, false);
                return new ViewHolder(view);
            }
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
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

        holder.numberAndTime.setText(StringUtils.getNumberAndTimeString(number, name, trip, time));
        if (!boardId.equals("b")) holder.subject.setText(Html.fromHtml(subject));
        else holder.subject.setVisibility(View.GONE);
        if (subject.equals("")) holder.subject.setVisibility(View.GONE);
        holder.comment.setText(Html.fromHtml(comment));
        holder.comment.setMovementMethod(LinkMovementMethod.getInstance());
        holder.postsAndFiles.setText(StringUtils.getCorrectPostsAndFilesString(postsCount, filesCount));

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
                setupImageContainer(holder, holder.image, holder.webmImageView, (short) 0, holder.summary,
                        thumbnail, file, size, width, height);
            } else if (files.size() <= 4) {
                switch (i) {
                    case 0: {
                        setupImageContainer(holder, holder.image1, holder.webmImageView1, (short) 0,
                                holder.summary1, thumbnail, file, size, width, height); break;
                    }
                    case 1: {
                        setupImageContainer(holder, holder.image2, holder.webmImageView2, (short) 1,
                                holder.summary2, thumbnail, file, size, width, height); break;
                    }
                    case 2: {
                        setupImageContainer(holder, holder.image3, holder.webmImageView3, (short) 2,
                                holder.summary3, thumbnail, file, size, width, height); break;
                    }
                    case 3: {
                        setupImageContainer(holder, holder.image4, holder.webmImageView4, (short) 3,
                                holder.summary4, thumbnail, file, size, width, height); break;
                    }
                }
            }
        }

        holder.threadItemContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.imageLoader.clearMemoryCache();
                mActivity.imageLoader.clearDiskCache();
                Intent intent = new Intent(mActivity, SingleThreadActivity.class);

                intent.putExtra(Constants.BOARD_ID, mActivity.boardId);
                intent.putExtra(Constants.BOARD_NAME, mActivity.boardName);
                intent.putExtra(Constants.THREAD_NUMBER, number);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mActivity.startActivity(intent);
                    mActivity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                } else mActivity.startActivity(intent);
            }
        });

        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, SingleThreadActivity.class);

                intent.putExtra(Constants.BOARD_ID, mActivity.boardId);
                intent.putExtra(Constants.BOARD_NAME, mActivity.boardName);
                intent.putExtra(Constants.THREAD_NUMBER, number);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mActivity.startActivity(intent);
                    mActivity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                } else mActivity.startActivity(intent);
            }
        });

        if ((position + 1) % 21 == 0) {
            if (position + 1 != mActivity.mSchema.getThreads().size()) {
                holder.indicatorView.setVisibility(View.VISIBLE);
                holder.indicatorTextView.setText(((holder.getAdapterPosition() + 1) / 21)
                        + " " + mActivity.getString(R.string.page_text));
                holder.indicatorTextView.setTypeface(null, Typeface.BOLD);
                holder.indicatorView.getLayoutParams().width =
                        holder.threadItemContainer.getLayoutParams().width;
            } else holder.indicatorView.setVisibility(View.GONE);
        } else {
            holder.indicatorView.setVisibility(View.GONE);
        }

    }

    private void switchImagesVisibility(ViewHolder holder, int filesSize) {
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

    private class ThumbnailOnClickListener implements View.OnClickListener {
        private int threadPosition;
        private int thumbnailPosition;

        public ThumbnailOnClickListener(int threadPosition, int thumbnailPosition) {
            this.threadPosition = threadPosition;
            this.thumbnailPosition = thumbnailPosition;
        }

        @Override
        public void onClick(View v) {
            showFullPicVid(threadPosition, thumbnailPosition);
        }
    }

    private void setupImageContainer(ViewHolder holder, ImageView image, ImageView webmImageView,
                                     short thumbnailPosition, TextView summary, String thumbnail,
                                     Files file, String imageOrVideoSize, String imageOrVideoWidth,
                                     String imageOrVideoHeight) {

        loadThumbnailPreview(thumbnail, image, imageOrVideoWidth, imageOrVideoHeight);

        ThumbnailOnClickListener thumbnailOnClickListener =
                new ThumbnailOnClickListener(holder.getAdapterPosition(), thumbnailPosition);

        if (file.getPath().substring(file.getPath().length() - 4,
                file.getPath().length()).equals(Formats.WEBM)) {
            webmImageView.setVisibility(View.VISIBLE);
            webmImageView.setOnClickListener(thumbnailOnClickListener);
        } else webmImageView.setVisibility(View.GONE);

        image.setOnClickListener(thumbnailOnClickListener);
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

    @Override
    public int getItemCount() {
        return mActivity.mSchema.getThreads().size();
    }

    @Override
    public int getItemViewType(int position) {
        List<Files> files = mActivity.mSchema.getThreads().get(position).getFiles();
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


    @SuppressLint("UseSparseArrays")
    private void showFullPicVid(final int threadPosition, final int thumbnailPosition) {
        if (DeviceUtils.sdkIsKitkatOrHigher()) {
            UiUtils.showSystemUI(mActivity);
            UiUtils.setBarsTranslucent(mActivity, true);
        }

        mActivity.fullPicVidOpenedAndFullScreenModeIsOn = false;
        mActivity.picVidToolbarContainer.setVisibility(View.VISIBLE);
        mActivity.fullPicVidOpened = true;

        ThreadsActivity.files = mActivity.mSchema.getThreads().get(threadPosition).getFiles();
        ThreadsActivity.imageCachePaths = new ArrayList<>();
        ThreadsActivity.galleryFragments = new HashMap<>();

        UiUtils.barsAreShown = true;

        mActivity.picVidPagerAdapter = new PicVidPagerAdapter(
                mActivity.getSupportFragmentManager(),
                mActivity, ThreadsActivity.files, thumbnailPosition);

        mActivity.picVidPager.setAdapter(mActivity.picVidPagerAdapter);
        mActivity.picVidPager.setOffscreenPageLimit(1);
        mActivity.picVidPager.setCurrentItem(thumbnailPosition);
        mActivity.picVidOpenedPosition = thumbnailPosition;
        mActivity.fullPicVidContainer.setVisibility(View.VISIBLE);
        mActivity.threadsViewPagerOnPageChangeListener = new ThreadsViewPagerOnPageChangeListener(mActivity);
        mActivity.picVidPager.addOnPageChangeListener(mActivity.threadsViewPagerOnPageChangeListener);

        String displayName = ThreadsActivity.files.get(thumbnailPosition).getDisplayName();
        if (displayName == null || displayName.equals("") || displayName.equals(" ")) {
            mActivity.picVidToolbarTitleTextView.setText(mActivity.getString(R.string.noname_text));
        } else {
            mActivity.picVidToolbarTitleTextView.setText(displayName);
        }
        mActivity.picVidToolbarTitleTextView.setTypeface(Typeface.DEFAULT_BOLD);
        mActivity.picVidToolbarTitleTextView.setTextSize(16.0f);
        mActivity.picVidToolbarShortInfoTextView.setText(StringUtils.getShortInfoForToolbarString(
                mActivity.picVidToolbarShortInfoTextView, thumbnailPosition, ThreadsActivity.files));
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

}
