package com.koresuniku.wishmaster.ui.text;

import android.text.Layout;
import android.text.Spannable;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;

import com.koresuniku.wishmaster.activities.SingleThreadActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AnswersLinkMovementMethod extends LinkMovementMethod {
    private final String TAG = AnswersLinkMovementMethod.class.getSimpleName();

    private SingleThreadActivity mActivity;
    private List<List<Integer>> mLocations;
   // private BackgroundColorSpan backgroundColorSpan;
    private int mPosition;

    public AnswersLinkMovementMethod(SingleThreadActivity activity, int position) {
        super();
        mActivity = activity;
        mLocations = new ArrayList<>();
        mPosition = position;
    }

    public static AnswersLinkMovementMethod getInstance(SingleThreadActivity activity, int position) {
        return new AnswersLinkMovementMethod(activity, position);
    }


    int begin = -1, end = -1;
    @Override
    public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {

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
            int off = layout.getOffsetForHorizontal(line, x);

            Log.d(TAG, "off: " + off);

            locateAnswersToBeColored(buffer);
            Log.d(TAG, "mAnswersSpansLocations: " + mLocations);

            for (List<Integer> locations : mLocations) {
                if (off >= locations.get(0) - 2 && off <= locations.get(1)) {
                    begin = locations.get(0) + 2;
                    end = locations.get(1);
                    buffer.setSpan(mActivity.adapter.foregroundColorSpan, locations.get(0),
                            locations.get(1), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    List<Integer> locationsToAdd;
                    locationsToAdd = mActivity.adapter.mAnswersSpansLocations.get(mPosition);
                    mActivity.adapter.mAnswersSpansLocations.remove(mPosition);
                    locationsToAdd.clear();
                    locationsToAdd.add(locations.get(0));
                    locationsToAdd.add(locations.get(1));
                    mActivity.adapter.mAnswersSpansLocations.add(mPosition, locationsToAdd);
                    Log.d(TAG, "adapter.mAnswersSpansLocations: " + mActivity.adapter.mAnswersSpansLocations);
                }
            }
        }
        if (action == MotionEvent.ACTION_UP) {
            if (begin != -1 && end != -1) {
                mActivity.showAnswer(String.valueOf(buffer.subSequence(begin, end)));
            }
        }

        if (action == MotionEvent.ACTION_CANCEL) {
            buffer.removeSpan(mActivity.adapter.foregroundColorSpan);
            mActivity.adapter.mAnswersSpansLocations.remove(mPosition);
            mActivity.adapter.mAnswersSpansLocations.add(mPosition, new ArrayList<Integer>());
        }

        return super.onTouchEvent(widget, buffer, event);
    }

    private void locateAnswersToBeColored(Spannable buffer) {
        mLocations = new ArrayList<>();
        int begin, end = -1;
        char c;
        StringBuilder answer = new StringBuilder();
        for (int i = 0; i < buffer.length(); i++) {
            c = buffer.charAt(i);
            if (c >= '0' && c <= '9') {
                begin = i;
                answer.append(c);
                while (c >= '0' && c <= '9') {
                    i++;
                    if (i == buffer.length()) break;
                    c = buffer.charAt(i);
                    answer.append(c);
                }
                answer.deleteCharAt(answer.length() - 1);
                end = i;
                mLocations.add(new ArrayList<>(Arrays.asList(begin - 2, end)));
                answer = new StringBuilder();
            }
        }
    }

    @Override
    public void initialize(TextView widget, Spannable text) {
        super.initialize(widget, text);

    }

}
