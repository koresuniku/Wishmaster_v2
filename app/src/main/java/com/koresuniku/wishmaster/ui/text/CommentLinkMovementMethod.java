package com.koresuniku.wishmaster.ui.text;

import android.app.Activity;
import android.text.Layout;
import android.text.Spannable;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;

import com.koresuniku.wishmaster.ui.activity.SingleThreadActivity;
import com.koresuniku.wishmaster.ui.activity.ThreadsActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommentLinkMovementMethod extends LinkMovementMethod implements IAllowActionCancel {
    private final String TAG = CommentLinkMovementMethod.class.getSimpleName();

    private Activity mActivity;
    private List<List<Integer>> mLocations;
    private int mPosition;
    public boolean allowActionCancel = true;
    private Spannable mBuffer;


    public CommentLinkMovementMethod(Activity activity, int position) {
        super();
        mActivity = activity;
        mLocations = new ArrayList<>();
        mPosition = position;
    }

    public static CommentLinkMovementMethod getInstance(Activity activity, int position) {
        return new CommentLinkMovementMethod(activity, position);
    }

    int begin = -1, end = -1;
    List<Integer> locationsToAdd;
    int off = 0;
    @Override
    public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
        // Get the event action
        int action = event.getAction();

        if (action == MotionEvent.ACTION_DOWN) {
            // Locate the area that was pressed
            int x = (int) event.getX();
            int y = (int) event.getY();
            x -= widget.getTotalPaddingLeft();
            y -= widget.getTotalPaddingTop();
            x += widget.getScrollX();
            y += widget.getScrollY();

            // Locate the URL text
            Layout layout = widget.getLayout();
            int line = layout.getLineForVertical(y);
            off = layout.getOffsetForHorizontal(line, x);

            // Find the URL that was pressed
            URLSpan[] link = buffer.getSpans(off, off, URLSpan.class);


            if (link.length != 0) {
                Log.d(TAG, "span start: " + buffer.getSpanStart(link[0]) + ", span end " + buffer.getSpanEnd(link[0]));

                locateAnswersToBeColored(buffer);
                Log.d(TAG, "mLocations: " + mLocations);
                boolean isAnswerFound = false;
                for (List<Integer> locations : mLocations) {
                    if (off >= locations.get(0) - 2 && off <= locations.get(1) - 1) {
                        isAnswerFound = true;
                        begin = locations.get(0) + 2;
                        end = locations.get(1);
                        if (mActivity instanceof ThreadsActivity) {

                        }
                        if (mActivity instanceof SingleThreadActivity) {
                            buffer.setSpan(((SingleThreadActivity)mActivity).adapter.foregroundColorSpan, locations.get(0),
                                    locations.get(1), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            locationsToAdd = ((SingleThreadActivity)mActivity).adapter.mCommentAnswersSpansLocations.get(mPosition);
                            ((SingleThreadActivity)mActivity).adapter.mCommentAnswersSpansLocations.remove(mPosition);
                            locationsToAdd.clear();
                            locationsToAdd.add(locations.get(0));
                            locationsToAdd.add(locations.get(1));
                            ((SingleThreadActivity)mActivity).adapter.mCommentAnswersSpansLocations.add(mPosition, locationsToAdd);
                        }
                    }
                }
                if (!isAnswerFound) {

                    if (mActivity instanceof ThreadsActivity) {
//                        buffer.setSpan(((ThreadsActivity)mActivity).adapter.backgroundColorSpan,
//                                buffer.getSpanStart(link[0]), buffer.getSpanEnd(link[0]),
//                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    if (mActivity instanceof SingleThreadActivity) {
                        buffer.setSpan(((SingleThreadActivity)mActivity).adapter.backgroundColorSpan,
                                buffer.getSpanStart(link[0]), buffer.getSpanEnd(link[0]),
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }

                }
            }
        }

        if (action == MotionEvent.ACTION_UP) {
            if (mActivity instanceof ThreadsActivity) {
                //buffer.removeSpan(((ThreadsActivity)mActivity).adapter.backgroundColorSpan);
            }
            if (mActivity instanceof SingleThreadActivity) {
                buffer.removeSpan(((SingleThreadActivity)mActivity).adapter.backgroundColorSpan);
                if (begin != -1 && end != -1) {
                    Log.d(TAG, "action_up");
                    disallowActionCancel();
                    ((SingleThreadActivity)mActivity).mAnswersManager.showAnswer(String.valueOf(buffer.subSequence(
                            begin, String.valueOf(buffer).contains("(OP)") ? end - 5 : end)),
                            ((SingleThreadActivity)mActivity).mPosts.get(mPosition).getNum(), true);
                }
            }

        }

        if (action == MotionEvent.ACTION_CANCEL) {
            Log.d(TAG, "actioncancel:");
            if (mActivity instanceof ThreadsActivity) {
                buffer.removeSpan(((ThreadsActivity)mActivity).mRecyclerViewUnit.getMRecyclerViewAdapter().backgroundColorSpan);
            }
            if (mActivity instanceof SingleThreadActivity) {
                buffer.removeSpan(((SingleThreadActivity)mActivity).adapter.backgroundColorSpan);
                URLSpan[] link = buffer.getSpans(off, off, URLSpan.class);
                if (link.length != 0) {
                    locateAnswersToBeColored(buffer);
                    for (List<Integer> locations : mLocations) {
                        if (off >= locations.get(0) - 2 && off <= locations.get(1) - 1) {
                            if (allowActionCancel) {
                                buffer.removeSpan(((SingleThreadActivity)mActivity).adapter.foregroundColorSpan);
                                ((SingleThreadActivity)mActivity).adapter.mCommentAnswersSpansLocations.remove(mPosition);
                                ((SingleThreadActivity)mActivity).adapter.mCommentAnswersSpansLocations.add(mPosition, new ArrayList<Integer>());
                            }
                        }
                    }
                }

            }

        }

        return super.onTouchEvent(widget, buffer, event);
    }

    private boolean answerIsToOp(Spannable buffer, int begin, int end) {
        return String.valueOf(buffer.subSequence(begin, end))
                .equals(((SingleThreadActivity)mActivity).mPosts.get(0).getNum());
    }

    private void locateAnswersToBeColored(Spannable buffer) {
        mLocations = new ArrayList<>();
        URLSpan[] urlSpans = buffer.getSpans(0, buffer.length(), URLSpan.class);
        int begin = 0, end;
        char c;
        for (URLSpan urlSpan : urlSpans) {
            String linkString = urlSpan.getURL();
            if (linkString.contains("http") || linkString.contains("ftp")) {
                //ToDo: handle http and ftp
            } else {
                //String answer = linkString.substring(linkString.indexOf('#') + 1, linkString.length());
                for (int i = begin; i < buffer.length(); i++) {
                    c = buffer.charAt(i);
                    if (c >= '0' && c <= '9') {
                        begin = i;
                        while (c >= '0' && c <= '9') {
                            i++;
                            if (i == buffer.length()) break;
                            c = buffer.charAt(i);
                        }
                        end = i;
                        mLocations.add(new ArrayList<>(Arrays.asList(
                                begin - 2, answerIsToOp(buffer, begin, end) ? end + 5 : end)));
                        begin = i;
                    }
                }
            }
        }

    }

    public List<List<Integer>> getActualLocations() {
        locateAnswersToBeColored(mBuffer);
        return mLocations;
    }

    public void setForegroundSpanForParticularLocation(String number) {
        locateAnswersToBeColored(mBuffer);
        //Log.d(TAG, "setForegroundSpanForParticularLocation: mBuffer: " + mBuffer.toString());
        //Log.d(TAG, "setForegroundSpanForParticularLocation: mLocations: " + mLocations);


            for (List<Integer> locations : mLocations) {
                int end = locations.get(1);
                if (String.valueOf(mBuffer.subSequence(locations.get(0) + 2, end)).contains("(OP)")) {
                    end -= 5;
                }
                if (String.valueOf(mBuffer.subSequence(locations.get(0) + 2, end)).equals(number)) {
                    //Log.d(TAG, "setForegroundSpanForParticularLocation: needa spanen " + locations.getByteInputStreamFromUrl(0) + ", " + locations.getByteInputStreamFromUrl(1));
                    mBuffer.setSpan(((SingleThreadActivity)mActivity).adapter.foregroundColorSpan, locations.get(0),
                            locations.get(1), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    List<Integer> locationsToAdd;
                    locationsToAdd = ((SingleThreadActivity)mActivity).adapter.mCommentAnswersSpansLocations.get(mPosition);
                    ((SingleThreadActivity)mActivity).adapter.mCommentAnswersSpansLocations.remove(mPosition);
                    locationsToAdd.clear();
                    locationsToAdd.add(locations.get(0));
                    locationsToAdd.add(locations.get(1));
                    ((SingleThreadActivity)mActivity).adapter.mCommentAnswersSpansLocations.add(mPosition, locationsToAdd);
                } else {
                    //Log.d(TAG, "setForegroundSpanForParticularLocation: " + mBuffer.subSequence(locations.getByteInputStreamFromUrl(0) + 2, end) + " != " + number);
                }
            }

    }

    @Override
    public void initialize(TextView widget, Spannable text) {
        this.mBuffer = text;
        //Log.d(TAG, "initialize " + mPosition);
        super.initialize(widget, text);
    }

    @Override
    public void allowActionCancel() {
        allowActionCancel = true;
    }

    @Override
    public void disallowActionCancel() {
        allowActionCancel = false;
    }
}
