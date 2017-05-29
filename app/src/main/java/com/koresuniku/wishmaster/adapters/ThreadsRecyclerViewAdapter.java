package com.koresuniku.wishmaster.adapters;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.koresuniku.wishmaster.activities.ThreadsActivity;
import com.koresuniku.wishmaster.http.threads_api.models.Files;
import com.koresuniku.wishmaster.http.threads_api.models.Thread;
import com.koresuniku.wishmaster.utils.DeviceUtils;
import com.koresuniku.wishmaster.utils.Constants;
import com.koresuniku.wishmaster.utils.Formats;
import com.koresuniku.wishmaster.ui.UIUtils;
import com.koresuniku.wishmaster.utils.listeners.ThreadsViewPagerOnPageChangeListener;

import java.util.ArrayList;
import java.util.List;


public class ThreadsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
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
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case Constants.ITEM_NO_IMAGES: {
                mViewType = viewType;
                view = LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.thread_item_no_imagies, parent, false);
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
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        //Log.d(LOG_TAG, "onViewDetachedFromWindow: ");

        System.gc();
    }

    private void ornutVGolosinu() {
        Toast toast = Toast.makeText(mActivity, "GIFKA!!!!!!", Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        System.gc();
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

        ((ViewHolder)holder).numberAndTime.setText("№" + number + (name.equals("") ? "" : " "
                + name) + " " + (trip.equals("") ? "" : " " + trip) + time);
        if (!boardId.equals("b")) {
            ((ViewHolder)holder).subject.setText(Html.fromHtml(subject));
        } else {
            ((ViewHolder)holder).subject.setVisibility(View.GONE);
        }
        if (subject.equals("")) ((ViewHolder)holder).subject.setVisibility(View.GONE);
        ((ViewHolder)holder).comment.setText(Html.fromHtml(comment));
        ((ViewHolder)holder).comment.setMovementMethod(LinkMovementMethod.getInstance());
        ((ViewHolder)holder).postsAndFiles.setText(correctPostsAndFilesString(postsCount, filesCount));

        String width;
        String height;
        String thumbnail;
        String size;

        int filesSize = files.size();
            switch (filesSize) {
                case 1: {
                    if (((ViewHolder)holder).imageAndSummaryContainer1 != null)
                        ((ViewHolder)holder).imageAndSummaryContainer1.setVisibility(View.VISIBLE);
                    if (((ViewHolder)holder).imageAndSummaryContainer2 != null)
                        ((ViewHolder)holder).imageAndSummaryContainer2.setVisibility(View.GONE);
                    if (((ViewHolder)holder).imageAndSummaryContainer3 != null)
                        ((ViewHolder)holder).imageAndSummaryContainer3.setVisibility(View.GONE);
                    if (((ViewHolder)holder).imageAndSummaryContainer4 != null)
                        ((ViewHolder)holder).imageAndSummaryContainer4.setVisibility(View.GONE);
                    break;
                }
                case 2: {
                    if (((ViewHolder)holder).imageAndSummaryContainer1 != null)
                        ((ViewHolder)holder).imageAndSummaryContainer1.setVisibility(View.VISIBLE);
                    if (((ViewHolder)holder).imageAndSummaryContainer2 != null)
                        ((ViewHolder)holder).imageAndSummaryContainer2.setVisibility(View.VISIBLE);
                    if (((ViewHolder)holder).imageAndSummaryContainer3 != null)
                        ((ViewHolder)holder).imageAndSummaryContainer3.setVisibility(View.GONE);
                    if (((ViewHolder)holder).imageAndSummaryContainer4 != null)
                        ((ViewHolder)holder).imageAndSummaryContainer4.setVisibility(View.GONE);
                    break;
                }
                case 3: {
                    if (((ViewHolder)holder).imageAndSummaryContainer1 != null)
                        ((ViewHolder)holder).imageAndSummaryContainer1.setVisibility(View.VISIBLE);
                    if (((ViewHolder)holder).imageAndSummaryContainer2 != null)
                        ((ViewHolder)holder).imageAndSummaryContainer2.setVisibility(View.VISIBLE);
                    if (((ViewHolder)holder).imageAndSummaryContainer3 != null)
                        ((ViewHolder)holder).imageAndSummaryContainer3.setVisibility(View.VISIBLE);
                    if (((ViewHolder)holder).imageAndSummaryContainer4 != null)
                        ((ViewHolder)holder).imageAndSummaryContainer4.setVisibility(View.GONE);
                    break;
                }
                case 4: {
                    if (((ViewHolder)holder).imageAndSummaryContainer1 != null)
                        ((ViewHolder)holder).imageAndSummaryContainer1.setVisibility(View.VISIBLE);
                    if (((ViewHolder)holder).imageAndSummaryContainer2 != null)
                        ((ViewHolder)holder).imageAndSummaryContainer2.setVisibility(View.VISIBLE);
                    if (((ViewHolder)holder).imageAndSummaryContainer3 != null)
                        ((ViewHolder)holder).imageAndSummaryContainer3.setVisibility(View.VISIBLE);
                    if (((ViewHolder)holder).imageAndSummaryContainer4 != null)
                        ((ViewHolder)holder).imageAndSummaryContainer4.setVisibility(View.VISIBLE);
                    break;
                }
            }

        for (int i = 0; i < files.size(); i++) {
            Files file = files.get(i);

            width = file.getWidth();
            height = file.getHeight();
            thumbnail = file.getThumbnail();
            size = file.getSize();

            if (file.getPath().substring(file.getPath().length() - 3, file.getPath().length()).equals("gif")) {
                ornutVGolosinu();
            } else //Log.d(LOG_TAG, file.getPath().substring(file.getPath().length() - 3, file.getPath().length()));


            if (files.size() == 1) {
                setImageViewWidthDependingOnOrientation(
                        mActivity.getResources().getConfiguration(), ((ViewHolder)holder).image);
                //mActivity.imageLoader.clearMemoryCache();
               // mActivity.imageLoader.displayImage(Constants.DVACH_BASE_URL + thumbnail, ((ViewHolder)holder).image);
                loadThumbnailPreview(thumbnail, ((ViewHolder)holder).image);

                if (file.getPath().substring(file.getPath().length() - 4, file.getPath().length()).equals(Formats.WEBM)) {
                    ((ViewHolder)holder).webmImageView.setVisibility(View.VISIBLE);
                    ((ViewHolder)holder).webmImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showFullPicVid(position, 0);
                        }
                    });
                } else ((ViewHolder)holder).webmImageView.setVisibility(View.GONE);
                ((ViewHolder)holder).image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showFullPicVid(position, 0);
                    }
                });

                ((ViewHolder)holder).summary.setText(size + "Кб, " + width + "x" + height);
//                ((ViewHolder)holder).summary.setLines(((ViewHolder)holder).summary.getLineCount());
            } else if (files.size() <= 4) {
                switch (i) {
                    case 0: {
//                        mActivity.imageLoader.clearMemoryCache();
//                        mActivity.imageLoader.displayImage(Constants.DVACH_BASE_URL + thumbnail, ((ViewHolder)holder).image1);
                        loadThumbnailPreview(thumbnail, ((ViewHolder)holder).image1);
                        setImageViewWidthDependingOnOrientation(
                                mActivity.getResources().getConfiguration(), ((ViewHolder)holder).image1);
                        if (file.getPath().substring(file.getPath().length() - 4, file.getPath().length()).equals(Formats.WEBM)) {
                            ((ViewHolder)holder).webmImageView1.setVisibility(View.VISIBLE);
                            ((ViewHolder)holder).webmImageView1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    showFullPicVid(position, 0);
                                }
                            });
                        } else ((ViewHolder)holder).webmImageView1.setVisibility(View.GONE);
                        ((ViewHolder)holder).image1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                showFullPicVid(position, 0);
                            }
                        });
                        ((ViewHolder)holder).summary1.setText(size + "Кб, " + width + "x" + height);
                        break;
                    }
                    case 1: {
//                        mActivity.imageLoader.clearMemoryCache();
//                        mActivity.imageLoader.displayImage(Constants.DVACH_BASE_URL + thumbnail, ((ViewHolder)holder).image2);
                        loadThumbnailPreview(thumbnail, ((ViewHolder)holder).image2);
                        setImageViewWidthDependingOnOrientation(
                                mActivity.getResources().getConfiguration(), ((ViewHolder)holder).image2);
                        if (file.getPath().substring(file.getPath().length() - 4, file.getPath().length()).equals(Formats.WEBM)) {
                            ((ViewHolder)holder).webmImageView2.setVisibility(View.VISIBLE);
                            ((ViewHolder)holder).webmImageView2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    showFullPicVid(position, 1);
                                }
                            });
                        } else ((ViewHolder)holder).webmImageView2.setVisibility(View.GONE);
                        ((ViewHolder)holder).image2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                showFullPicVid(position, 1);
                            }
                        });
                        ((ViewHolder)holder).summary2.setText(size + "Кб, " + width + "x" + height);
                        break;
                    }
                    case 2: {
//                        mActivity.imageLoader.clearMemoryCache();
//                        //mActivity.imageLoader.clearDiskCache();
//                        mActivity.imageLoader.displayImage(Constants.DVACH_BASE_URL + thumbnail, ((ViewHolder)holder).image3);
                        loadThumbnailPreview(thumbnail, ((ViewHolder)holder).image3);
                        setImageViewWidthDependingOnOrientation(
                                mActivity.getResources().getConfiguration(), ((ViewHolder)holder).image3);
                        if (file.getPath().substring(file.getPath().length() - 4, file.getPath().length()).equals(Formats.WEBM)) {
                            ((ViewHolder)holder).webmImageView3.setVisibility(View.VISIBLE);
                            ((ViewHolder)holder).webmImageView3.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    showFullPicVid(position, 2);
                                }
                            });
                        } else ((ViewHolder)holder).webmImageView3.setVisibility(View.GONE);
                        ((ViewHolder)holder).image3.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                showFullPicVid(position, 2);
                            }
                        });
                        ((ViewHolder)holder).summary3.setText(size + "Кб, " + width + "x" + height);
                        break;
                    }
                    case 3: {
//                        mActivity.imageLoader.clearMemoryCache();
//                        //mActivity.imageLoader.clearDiskCache();
//                        mActivity.imageLoader.displayImage(Constants.DVACH_BASE_URL + thumbnail, ((ViewHolder)holder).image4);
                        loadThumbnailPreview(thumbnail, ((ViewHolder)holder).image4);
                        setImageViewWidthDependingOnOrientation(
                                mActivity.getResources().getConfiguration(), ((ViewHolder)holder).image4);
                        if (file.getPath().substring(file.getPath().length() - 4, file.getPath().length()).equals(Formats.WEBM)) {
                            ((ViewHolder)holder).webmImageView4.setVisibility(View.VISIBLE);
                            ((ViewHolder)holder).webmImageView4.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    showFullPicVid(position, 3);
                                }
                            });
                        } else ((ViewHolder)holder).webmImageView4.setVisibility(View.GONE);
                        ((ViewHolder)holder).image4.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                showFullPicVid(position, 3);
                            }
                        });
                        ((ViewHolder)holder).summary4.setText(size + "Кб, " + width + "x" + height);
                        break;
                    }
                }
            }
        }

        ((ViewHolder)holder).threadItemContainer.setOnClickListener(new View.OnClickListener() {
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

        ((ViewHolder)holder).comment.setOnClickListener(new View.OnClickListener() {
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

        if ((position + 1) % 21 == 0) {

            if (position + 1 != mActivity.mSchema.getThreads().size()) {
                //Log.d(LOG_TAG, "setting textview");
                ((ViewHolder)holder).indicatorView.setVisibility(View.VISIBLE);
                ((ViewHolder)holder).indicatorTextView.setText(((position + 1) / 21) + " " + mActivity.getString(R.string.page_text));
                ((ViewHolder)holder).indicatorTextView.setTypeface(null, Typeface.BOLD);
            } else ((ViewHolder)holder).indicatorView.setVisibility(View.GONE);
        } else {
            //Log.d(LOG_TAG, "position++: " + position);
            ((ViewHolder)holder).indicatorView.setVisibility(View.GONE);
        }

    }

    private void loadThumbnailPreview(String thumbnail, ImageView image) {
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
            public boolean onResourceReady(Bitmap resource, Uri model,
                                           Target<Bitmap> target, boolean isFromMemoryCache,
                                           boolean isFirstResource) {
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


    private void showFullPicVid(final int threadPosition, final int thumbnailPosition) {
        if (DeviceUtils.getApiInt() >= 19) UIUtils.showSystemUI(mActivity);
        if (DeviceUtils.getApiInt() >= 19) UIUtils.setBarsTranslucent(mActivity, true);
        mActivity.fullPicVidOpenedAndFullScreenModeIsOn = false;
        mActivity.picVidToolbarContainer.setVisibility(View.VISIBLE);
        mActivity.fullPicVidOpened = true;

        ThreadsActivity.files = mActivity.mSchema.getThreads().get(threadPosition).getFiles();
        ThreadsActivity.imageCachePaths = new ArrayList<>();

        UIUtils.barsAreShown = true;

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
            mActivity.picVidToolbarTitleTextView.setText("noname.hz");
        } else {
            mActivity.picVidToolbarTitleTextView.setText(displayName);
        }
        mActivity.picVidToolbarTitleTextView.setTypeface(Typeface.DEFAULT_BOLD);
        mActivity.picVidToolbarTitleTextView.setTextSize(16.0f);
        mActivity.picVidToolbarShortInfoTextView.setText("(" + (thumbnailPosition + 1) + "/"
        + ThreadsActivity.files.size() + "), " + ThreadsActivity.files.get(thumbnailPosition).getWidth() + "x"
        + ThreadsActivity.files.get(thumbnailPosition).getHeight() + ", "
                + ThreadsActivity.files.get(thumbnailPosition).getSize() + " кб");


    }

    private void setImageViewWidthDependingOnOrientation(
            Configuration configuration, ImageView imageView) {
        if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            imageView.getLayoutParams().width = DeviceUtils.apiIs20OrHigher() ? 160 : 80;
            imageView.requestLayout();
        } else {
            imageView.getLayoutParams().width = DeviceUtils.apiIs20OrHigher() ? 200 : 100;
            imageView.requestLayout();
        }
    }



    private String correctPostsAndFilesString(String posts, String files) {
        String result = "Пропущено ";

        Integer postsLastNumeral =
                Integer.parseInt(posts.substring(posts.length() - 1, posts.length()));
        Integer filesLastNUmeral =
                Integer.parseInt(files.substring(files.length() - 1, files.length()));

        switch (postsLastNumeral) {
            case 0: {
                result += posts + " постов, ";
                break;
            }
            case 1: {
                result += posts + " пост, ";
                break;
            }
            case 2: {
                result += posts + " поста, ";
                break;
            }
            case 3: {
                result += posts + " поста, ";
                break;
            }
            case 4: {
                result += posts + " поста, ";
                break;
            }
            case 5: {
                result += posts + " постов, ";
                break;
            }
            case 6: {
                result += posts + " постов, ";
                break;
            }
            case 7: {
                result += posts + " постов, ";
                break;
            }
            case 8: {
                result += posts + " постов, ";
                break;
            }
            case 9: {
                result += posts + " постов, ";
                break;
            }
        }

        switch (filesLastNUmeral) {
            case 0: {
                result += files + " файлов";
                break;
            }
            case 1: {
                result += files + " файл";
                break;
            }
            case 2: {
                result += files + " файла";
                break;
            }
            case 3: {
                result += files + " файла";
                break;
            }
            case 4: {
                result += files + " файла";
                break;
            }
            case 5: {
                result += files + " файлов";
                break;
            }
            case 6: {
                result += files + " файлов";
                break;
            }
            case 7: {
                result += files + " файлов";
                break;
            }
            case 8: {
                result += files + " файлов";
                break;
            }
            case 9: {
                result += files + " файлов";
                break;
            }
        }
        return result;
    }

}
