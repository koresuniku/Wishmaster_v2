package com.koresuniku.wishmaster.ui.text;

import android.text.Layout;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;

public class CommentLinkMovementMethod extends LinkMovementMethod {
    private final String TAG = CommentLinkMovementMethod.class.getSimpleName();
    public CommentLinkMovementMethod() {
        super();
    }

    public static CommentLinkMovementMethod getInstance() {
        return new CommentLinkMovementMethod();
    }

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
            int off = layout.getOffsetForHorizontal(line, x);

            Log.d(TAG, "off: " + off);

            // Find the URL that was pressed
            URLSpan[] link = buffer.getSpans(off, off, URLSpan.class);
            if (link.length != 0) {
                String linkString = link[0].getURL();
                if (linkString.contains("http")) {
                    //TODO: handle https'
                } else {
                    String answer = linkString.substring(linkString.indexOf('#') + 1, linkString.length());
                    Log.d(TAG, "onTouchEvent: answer: " + answer);
                }
            }
        }








        return super.onTouchEvent(widget, buffer, event);
    }

    @Override
    public void initialize(TextView widget, Spannable text) {
        super.initialize(widget, text);
    }
}
