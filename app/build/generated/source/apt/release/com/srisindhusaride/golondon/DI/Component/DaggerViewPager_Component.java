package com.srisindhusaride.golondon.DI.Component;

import com.srisindhusaride.golondon.DI.Module.Viewpager_module;
import com.srisindhusaride.golondon.DI.Module.Viewpager_module_ProvidesNearbyPagerAdapterFactory;
import com.srisindhusaride.golondon.Nearby_Fragment;
import com.srisindhusaride.golondon.Nearby_Fragment_MembersInjector;
import dagger.MembersInjector;
import dagger.internal.DoubleCheck;
import dagger.internal.Preconditions;
import javax.annotation.Generated;
import javax.inject.Provider;

@Generated(
  value = "dagger.internal.codegen.ComponentProcessor",
  comments = "https://google.github.io/dagger"
)
public final class DaggerViewPager_Component implements ViewPager_Component {
  private Provider<Nearby_Fragment.Nearby_PagerAdapter> providesNearbyPagerAdapterProvider;

  private MembersInjector<Nearby_Fragment> nearby_FragmentMembersInjector;

  private DaggerViewPager_Component(Builder builder) {
    assert builder != null;
    initialize(builder);
  }

  public static Builder builder() {
    return new Builder();
  }

  @SuppressWarnings("unchecked")
  private void initialize(final Builder builder) {

    this.providesNearbyPagerAdapterProvider =
        DoubleCheck.provider(
            Viewpager_module_ProvidesNearbyPagerAdapterFactory.create(builder.viewpager_module));

    this.nearby_FragmentMembersInjector =
        Nearby_Fragment_MembersInjector.create(providesNearbyPagerAdapterProvider);
  }

  @Override
  public void inject(Nearby_Fragment nearby_fragment) {
    nearby_FragmentMembersInjector.injectMembers(nearby_fragment);
  }

  public static final class Builder {
    private Viewpager_module viewpager_module;

    private Builder() {}

    public ViewPager_Component build() {
      if (viewpager_module == null) {
        throw new IllegalStateException(Viewpager_module.class.getCanonicalName() + " must be set");
      }
      return new DaggerViewPager_Component(this);
    }

    public Builder viewpager_module(Viewpager_module viewpager_module) {
      this.viewpager_module = Preconditions.checkNotNull(viewpager_module);
      return this;
    }
  }
}
