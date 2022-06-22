// Generated code from Butter Knife. Do not modify!
package com.b4ebusinessdriver.Activity;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.b4ebusinessdriver.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class AlertActivity_ViewBinding implements Unbinder {
  private AlertActivity target;

  private View view2131362247;

  private View view2131362513;

  @UiThread
  public AlertActivity_ViewBinding(AlertActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public AlertActivity_ViewBinding(final AlertActivity target, View source) {
    this.target = target;

    View view;
    target.title = Utils.findRequiredViewAsType(source, R.id.title, "field 'title'", TextView.class);
    target.message = Utils.findRequiredViewAsType(source, R.id.message, "field 'message'", TextView.class);
    view = Utils.findRequiredView(source, R.id.no_btn, "field 'noBtn' and method 'onNoBtnClicked'");
    target.noBtn = Utils.castView(view, R.id.no_btn, "field 'noBtn'", TextView.class);
    view2131362247 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onNoBtnClicked();
      }
    });
    view = Utils.findRequiredView(source, R.id.yes_btn, "field 'yesBtn' and method 'onYesBtnClicked'");
    target.yesBtn = Utils.castView(view, R.id.yes_btn, "field 'yesBtn'", TextView.class);
    view2131362513 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onYesBtnClicked();
      }
    });
  }

  @Override
  @CallSuper
  public void unbind() {
    AlertActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.title = null;
    target.message = null;
    target.noBtn = null;
    target.yesBtn = null;

    view2131362247.setOnClickListener(null);
    view2131362247 = null;
    view2131362513.setOnClickListener(null);
    view2131362513 = null;
  }
}
