package com.srisindhusaride.golondon;

import dagger.MembersInjector;
import javax.annotation.Generated;
import javax.inject.Provider;

@Generated(
  value = "dagger.internal.codegen.ComponentProcessor",
  comments = "https://google.github.io/dagger"
)
public final class Nearby_Fragment_MembersInjector implements MembersInjector<Nearby_Fragment> {
  private final Provider<Nearby_Fragment.Nearby_PagerAdapter> mNearby_viewpager_adapterProvider;

  public Nearby_Fragment_MembersInjector(
      Provider<Nearby_Fragment.Nearby_PagerAdapter> mNearby_viewpager_adapterProvider) {
    assert mNearby_viewpager_adapterProvider != null;
    this.mNearby_viewpager_adapterProvider = mNearby_viewpager_adapterProvider;
  }

  public static MembersInjector<Nearby_Fragment> create(
      Provider<Nearby_Fragment.Nearby_PagerAdapter> mNearby_viewpager_adapterProvider) {
    return new Nearby_Fragment_MembersInjector(mNearby_viewpager_adapterProvider);
  }

  @Override
  public void injectMembers(Nearby_Fragment instance) {
    if (instance == null) {
      throw new NullPointerException("Cannot inject members into a null reference");
    }
    instance.mNearby_viewpager_adapter = mNearby_viewpager_adapterProvider.get();
  }

  public static void injectMNearby_viewpager_adapter(
      Nearby_Fragment instance,
      Provider<Nearby_Fragment.Nearby_PagerAdapter> mNearby_viewpager_adapterProvider) {
    instance.mNearby_viewpager_adapter = mNearby_viewpager_adapterProvider.get();
  }
}
