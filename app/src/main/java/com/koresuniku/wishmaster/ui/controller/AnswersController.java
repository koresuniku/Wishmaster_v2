package com.koresuniku.wishmaster.ui.controller;

import android.content.res.Configuration;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.koresuniku.wishmaster.R;
import com.koresuniku.wishmaster.ui.activity.SingleThreadActivity;
import com.koresuniku.wishmaster.http.single_thread_api.models.Post;
import com.koresuniku.wishmaster.ui.UiUtils;
import com.koresuniku.wishmaster.ui.text.AnswersLinkMovementMethod;
import com.koresuniku.wishmaster.ui.text.CommentLinkMovementMethod;
import com.koresuniku.wishmaster.ui.text.IAllowActionCancel;
import com.koresuniku.wishmaster.ui.widget.SaveStateScrollView;
import com.koresuniku.wishmaster.util.DeviceUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.view.View.GONE;

public class AnswersController {
    private final String LOG_TAG = AnswersController.class.getSimpleName();

    private SingleThreadActivity mActivity;

    public AnswersController(SingleThreadActivity activity) {
        this.mActivity = activity;
    }

    public void showAnswer(String postNumberToGo, String postNumberFrom, boolean fromComment) {
        if (getAnswersMode() == 0) {
            if (fromComment) showSingleAnswer(postNumberToGo, postNumberFrom);
            else showMultipleAnswers(postNumberFrom);
        } else {
            showSingleAnswer(postNumberToGo, postNumberFrom);
        }
    }

    public void writeInAnswerScrollState() {
        Log.d(LOG_TAG, "writeInAnswerScrollState()");
        mActivity.mAnswersScrollStates.add(mActivity.mAnswerLayout.findViewById(R.id.answer_layout_scrollview).getScrollY());
    }

    public void showSingleAnswer(String postNumberToGo, String postNumberFrom) {
        if (DeviceUtils.getApiInt() >= 19) UiUtils.setStatusBarTranslucent(mActivity, true);

        int position = -1;
        for (Post post : mActivity.mPosts) {
            if (post.getNum().equals(postNumberToGo)) {
                position = mActivity.mPosts.indexOf(post);
                break;
            }
        }
        Log.d(LOG_TAG, "getting child for position: " + position);
        View answerView = mActivity.adapter.getViewForPosition(position);
        if (postNumberFrom != null) {
            if (((TextView)answerView.findViewById(R.id.post_comment)).getMovementMethod() instanceof CommentLinkMovementMethod) {
                ((CommentLinkMovementMethod) ((TextView) answerView.findViewById(R.id.post_comment)).getMovementMethod())
                        .setForegroundSpanForParticularLocation(postNumberFrom);
            }
            if (((TextView)answerView.findViewById(R.id.answers)).getMovementMethod() instanceof AnswersLinkMovementMethod) {
                ((AnswersLinkMovementMethod) ((TextView) answerView.findViewById(R.id.answers)).getMovementMethod())
                        .setForegroundSpanForParticularLocation(postNumberFrom);
            }
        }
        setupAnswersLayoutContainer(mActivity.getResources().getConfiguration());
        mActivity.mAnswerViews.add(new ArrayList<>(Collections.singletonList(answerView)));
        mActivity.mAnswerList.removeAllViews();
        mActivity.mAnswerList.addView(answerView);
        if (mActivity.answerOpened) {
            writeInAnswerScrollState();
        }
        mActivity.findViewById(R.id.answer_layout_container).setVisibility(View.VISIBLE);
        mActivity.answerOpened = true;
        mActivity.adapter.notifySingleView = true;
    }

    public void showMultipleAnswers(String postNumberFrom) {
        if (DeviceUtils.sdkIsKitkatOrHigher()) UiUtils.setStatusBarTranslucent(mActivity, true);

        Log.d(LOG_TAG, "postNumberFrom: " + postNumberFrom);

        List<String> answersNumbers = mActivity.adapter.mAnswers.get(postNumberFrom);
        List<Integer> answersPositions = new ArrayList<>();

        Post post;
        for (int i = 0; i < mActivity.mPosts.size(); i++) {
            post = mActivity.mPosts.get(i);
            if (answersNumbers.contains(post.getNum())) {
                answersPositions.add(mActivity.mPosts.indexOf(post));
            }
        }

        View answerView;
        mActivity.mAnswerViews.add(new ArrayList<View>());
        for (Integer answerPosition : answersPositions) {
            answerView = mActivity.adapter.getViewForPosition(answerPosition);
            if (((TextView)answerView.findViewById(R.id.post_comment)).getMovementMethod() instanceof CommentLinkMovementMethod) {
                ((CommentLinkMovementMethod) ((TextView) answerView.findViewById(R.id.post_comment)).getMovementMethod())
                        .setForegroundSpanForParticularLocation(postNumberFrom);
            }
            if (((TextView)answerView.findViewById(R.id.answers)).getMovementMethod() instanceof AnswersLinkMovementMethod) {
                ((AnswersLinkMovementMethod) ((TextView) answerView.findViewById(R.id.answers)).getMovementMethod())
                        .setForegroundSpanForParticularLocation(postNumberFrom);
            }
            mActivity.mAnswerViews.get(mActivity.mAnswerViews.size() - 1).addAll(Collections.singletonList(answerView));
        }
        setupAnswersLayoutContainer(mActivity.getResources().getConfiguration());

        mActivity.mAnswerList.removeAllViews();

        ImageView lineDivider;
        for (View view : mActivity.mAnswerViews.get(mActivity.mAnswerViews.size() - 1)) {
            mActivity.mAnswerList.addView(view);
            lineDivider = new ImageView(mActivity);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.height = DeviceUtils.sdkIsLollipopOrHigher() ? 2 : 1;
            lineDivider.setLayoutParams(params);
            lineDivider.setPadding(DeviceUtils.sdkIsLollipopOrHigher() ? 16 : 8, 0,
                    DeviceUtils.sdkIsLollipopOrHigher() ? 16 : 8, 0);
            lineDivider.setImageResource(android.R.color.darker_gray);
            lineDivider.setBackgroundColor(mActivity.getResources().getColor(R.color.common_background_color));
            mActivity.mAnswerList.addView(lineDivider);
        }
        mActivity.mAnswerList.removeViewAt(mActivity.mAnswerList.getChildCount() - 1);

        if (mActivity.answerOpened) writeInAnswerScrollState();

        ((SaveStateScrollView)mActivity.mAnswerLayout.findViewById(R.id.answer_layout_scrollview))
                .scrollToWithGuarantees(0, 0);

        mActivity.findViewById(R.id.answer_layout_container).setVisibility(View.VISIBLE);
        mActivity.answerOpened = true;
        mActivity.adapter.notifySingleView = true;
    }

    public void showPreviousAnswer() {
        if (mActivity.sharedPreferences.getInt(
                mActivity.getString(R.string.sp_answers_code),
                Integer.parseInt(mActivity.getString(R.string.sp_answers_default))) == 0) {
            showPreviousMultipleAnswers();
        } else {
            showPreviousSingleAnswer();
        }
    }

    public void showPreviousSingleAnswer() {
        Log.d(LOG_TAG, "sshowPreviousSingleAnswer()");

        mActivity.mAnswerList.removeAllViews();
        if (mActivity.mAnswerViews.size() <= 1) {
            closeAnswerViews();
            return;
        }
        mActivity.mAnswerViews.remove(mActivity.mAnswerViews.size() - 1);
//        if (((TextView)mAnswerViews.getByteInputStreamFromUrl(mAnswerViews.size() - 1).getByteInputStreamFromUrl(0)
//                .findViewById(R.id.answers)).getMovementMethod() instanceof AnswersLinkMovementMethod) {
//            ((AnswersLinkMovementMethod) ((TextView)mAnswerViews.getByteInputStreamFromUrl(mAnswerViews.size() - 1).getByteInputStreamFromUrl(0)
//                    .findViewById(R.id.answers)).getMovementMethod()).allowActionCancel = true;
//        } else if (((TextView)mAnswerViews.getByteInputStreamFromUrl(mAnswerViews.size() - 1).getByteInputStreamFromUrl(mAnswerViews.size() - 1)
//                .findViewById(R.id.post_comment)).getMovementMethod() instanceof CommentLinkMovementMethod) {
//            ((CommentLinkMovementMethod) ((TextView)mAnswerViews.getByteInputStreamFromUrl(mAnswerViews.size() - 1).getByteInputStreamFromUrl(0)
//                    .findViewById(R.id.post_comment)).getMovementMethod()).allowActionCancel = true;
//        }

        ((IAllowActionCancel)((TextView)mActivity.mAnswerViews.get(mActivity.mAnswerViews.size() - 1).get(0)
                .findViewById(R.id.answers)).getMovementMethod()).allowActionCancel();

        mActivity.mAnswerList.addView(mActivity.mAnswerViews.get(mActivity.mAnswerViews.size() - 1).get(0));
        Log.d(LOG_TAG, "scroll states: " + mActivity.mAnswersScrollStates.size());
        ((SaveStateScrollView)mActivity.mAnswerLayout.findViewById(R.id.answer_layout_scrollview))
                .scrollToWithGuarantees(0, mActivity.mAnswersScrollStates.get(mActivity.mAnswersScrollStates.size() - 1));

        mActivity.mAnswersScrollStates.remove(mActivity.mAnswerViews.size() - 1);
    }

    public void showPreviousMultipleAnswers() {
        Log.d(LOG_TAG, "showPreviousMultipleAnswers()");
        mActivity.mAnswerList.removeAllViews();
        if (mActivity.mAnswerViews.size() <= 1) {
            closeAnswerViews();
            return;
        }
        mActivity.mAnswerViews.remove(mActivity.mAnswerViews.size() - 1);

        ((IAllowActionCancel)((TextView)mActivity.mAnswerViews.get(mActivity.mAnswerViews.size() - 1).get(0)
                .findViewById(R.id.answers)).getMovementMethod()).allowActionCancel();

        ImageView lineDivider;
        for (View view : mActivity.mAnswerViews.get(mActivity.mAnswerViews.size() - 1)) {
            mActivity.mAnswerList.addView(view);
            lineDivider = new ImageView(mActivity);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.height = DeviceUtils.sdkIsLollipopOrHigher() ? 2 : 1;
            lineDivider.setLayoutParams(params);
            lineDivider.setPadding(DeviceUtils.sdkIsLollipopOrHigher() ? 16 : 8, 0,
                    DeviceUtils.sdkIsLollipopOrHigher() ? 16 : 8, 0);
            lineDivider.setImageResource(android.R.color.darker_gray);
            lineDivider.setBackgroundColor(mActivity.getResources().getColor(R.color.common_background_color));
            mActivity.mAnswerList.addView(lineDivider);
        }
        mActivity.mAnswerList.removeViewAt(mActivity.mAnswerList.getChildCount() - 1);

        ((SaveStateScrollView)mActivity.mAnswerLayout.findViewById(R.id.answer_layout_scrollview))
                .scrollToWithGuarantees(0, mActivity.mAnswersScrollStates.get(mActivity.mAnswersScrollStates.size() - 1));

        mActivity.mAnswersScrollStates.remove(mActivity.mAnswerViews.size() - 1);
    }

    public void closeAnswerViews() {
        Log.d(LOG_TAG, "closeAnswerViews:");
        mActivity.answerOpened = false;
        mActivity.adapter.notifySingleView = false;
        if (DeviceUtils.getApiInt() >= 19) UiUtils.setStatusBarTranslucent(mActivity, false);
        mActivity.mAnswerViews = new ArrayList<>();
        mActivity.mAnswersScrollStates = new ArrayList<>();
        mActivity.mAnswerList.removeAllViews();
        mActivity.findViewById(R.id.answer_layout_container).setVisibility(GONE);
    }

    public void setupAnswersLayoutContainer(Configuration configuration) {
        mActivity.findViewById(R.id.answer_layout_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPreviousAnswer();
            }
        });
        if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (DeviceUtils.getApiInt() >= 19) {
                mActivity.findViewById(R.id.answer_layout_container).setPadding(
                        0, DeviceUtils.sdkIsLollipopOrHigher() ? 48 : 24,
                        0, DeviceUtils.sdkIsLollipopOrHigher() ? 96 : 48);

                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.gravity = Gravity.CENTER;
                params.setMargins(
                        DeviceUtils.sdkIsLollipopOrHigher() ? 24 : 12,
                        DeviceUtils.sdkIsLollipopOrHigher() ? 50 : 24,
                        DeviceUtils.sdkIsLollipopOrHigher() ? 24 : 12,
                        DeviceUtils.sdkIsLollipopOrHigher() ? 50 : 25);
                mActivity.mAnswerLayout.setLayoutParams(params);
                mActivity.mAnswerLayout.requestLayout();
            }
        } else {
            if (DeviceUtils.getApiInt() >= 19) {
                mActivity.findViewById(R.id.answer_layout_container).setPadding(
                        0, DeviceUtils.sdkIsLollipopOrHigher() ? 48 : 24, 0, 0);

                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.gravity = Gravity.CENTER;
                params.setMargins(
                        DeviceUtils.sdkIsLollipopOrHigher() ? 24 : 12,
                        DeviceUtils.sdkIsLollipopOrHigher() ? 50 : 25,
                        DeviceUtils.sdkIsLollipopOrHigher() ? 24 : 12,
                        DeviceUtils.sdkIsLollipopOrHigher() ? 50 : 25);
                mActivity.mAnswerLayout.setLayoutParams(params);
                mActivity.mAnswerLayout.requestLayout();
            }
        }
    }

    public void setupAnswers() {
        mActivity.mAnswerViews = new ArrayList<>();
        mActivity.mAnswerLayout = (CardView) mActivity.findViewById(R.id.answer_layout);
        mActivity.mAnswerList = (LinearLayout) mActivity.findViewById(R.id.answer_layout_list);
        mActivity.mAnswersScrollStates = new ArrayList<>();
        mActivity.answerOpened = false;
    }

    public int getAnswersMode() {
        return mActivity.sharedPreferences.getInt(
                mActivity.getString(R.string.sp_answers_code),
                Integer.parseInt(mActivity.getString(R.string.sp_answers_default)));
    }


}
