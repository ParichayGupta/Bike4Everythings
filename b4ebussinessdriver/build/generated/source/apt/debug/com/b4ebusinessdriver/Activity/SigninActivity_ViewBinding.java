// Generated code from Butter Knife. Do not modify!
package com.b4ebusinessdriver.Activity;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.b4ebusinessdriver.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class SigninActivity_ViewBinding implements Unbinder {
  private SigninActivity target;

  private View view2131362386;

  private View view2131362388;

  private View view2131362089;

  @UiThread
  public SigninActivity_ViewBinding(SigninActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public SigninActivity_ViewBinding(final SigninActivity target, View source) {
    this.target = target;

    View view;
    target.mobile = Utils.findRequiredViewAsType(source, R.id.mobile, "field 'mobile'", EditText.class);
    target.mobileError = Utils.findRequiredViewAsType(source, R.id.mobile_error, "field 'mobileError'", TextView.class);
    target.password = Utils.findRequiredViewAsType(source, R.id.password, "field 'password'", EditText.class);
    view = Utils.findRequiredView(source, R.id.signin_btn, "field 'signinBtn' and method 'onViewClicked'");
    target.signinBtn = Utils.castView(view, R.id.signin_btn, "field 'signinBtn'", Button.class);
    view2131362386 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.signup_btn, "field 'signupBtn' and method 'onViewClicked'");
    target.signupBtn = Utils.castView(view, R.id.signup_btn, "field 'signupBtn'", Button.class);
    view2131362388 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.forget, "field 'forget' and method 'onViewClicked'");
    target.forget = Utils.castView(view, R.id.forget, "field 'forget'", TextView.class);
    view2131362089 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked();
      }
    });
  }

  @Override
  @CallSuper
  public void unbind() {
    SigninActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.mobile = null;
    target.mobileError = null;
    target.password = null;
    target.signinBtn = null;
    target.signupBtn = null;
    target.forget = null;

    view2131362386.setOnClickListener(null);
    view2131362386 = null;
    view2131362388.setOnClickListener(null);
    view2131362388 = null;
    view2131362089.setOnClickListener(null);
    view2131362089 = null;
  }
}
