// Generated code from Butter Knife. Do not modify!
package com.b4ebusinessdriver.Activity;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.b4ebusinessdriver.R;
import com.b4ebusinessdriver.Utils.CircleImageView;
import java.lang.IllegalStateException;
import java.lang.Override;

public class ProfileActivity_ViewBinding implements Unbinder {
  private ProfileActivity target;

  private View view2131361940;

  private View view2131362488;

  private View view2131362487;

  @UiThread
  public ProfileActivity_ViewBinding(ProfileActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public ProfileActivity_ViewBinding(final ProfileActivity target, View source) {
    this.target = target;

    View view;
    target.name = Utils.findRequiredViewAsType(source, R.id.name, "field 'name'", EditText.class);
    target.mobile = Utils.findRequiredViewAsType(source, R.id.mobile, "field 'mobile'", EditText.class);
    target.email = Utils.findRequiredViewAsType(source, R.id.email, "field 'email'", EditText.class);
    target.password = Utils.findRequiredViewAsType(source, R.id.password, "field 'password'", EditText.class);
    view = Utils.findRequiredView(source, R.id.changepassword, "field 'changepassword' and method 'onChangepasswordClicked'");
    target.changepassword = Utils.castView(view, R.id.changepassword, "field 'changepassword'", TextView.class);
    view2131361940 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onChangepasswordClicked();
      }
    });
    target.profileImage = Utils.findRequiredViewAsType(source, R.id.profileImage, "field 'profileImage'", CircleImageView.class);
    view = Utils.findRequiredView(source, R.id.updateImage, "field 'updateImage' and method 'onUpdateImageClicked'");
    target.updateImage = Utils.castView(view, R.id.updateImage, "field 'updateImage'", ImageView.class);
    view2131362488 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onUpdateImageClicked();
      }
    });
    view = Utils.findRequiredView(source, R.id.update, "field 'update' and method 'onUpdateClicked'");
    target.update = Utils.castView(view, R.id.update, "field 'update'", Button.class);
    view2131362487 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onUpdateClicked();
      }
    });
    target.switchActive = Utils.findRequiredViewAsType(source, R.id.switchActive, "field 'switchActive'", Switch.class);
    target.relativeLayout = Utils.findRequiredViewAsType(source, R.id.relativeLayout, "field 'relativeLayout'", RelativeLayout.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    ProfileActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.name = null;
    target.mobile = null;
    target.email = null;
    target.password = null;
    target.changepassword = null;
    target.profileImage = null;
    target.updateImage = null;
    target.update = null;
    target.switchActive = null;
    target.relativeLayout = null;

    view2131361940.setOnClickListener(null);
    view2131361940 = null;
    view2131362488.setOnClickListener(null);
    view2131362488 = null;
    view2131362487.setOnClickListener(null);
    view2131362487 = null;
  }
}
