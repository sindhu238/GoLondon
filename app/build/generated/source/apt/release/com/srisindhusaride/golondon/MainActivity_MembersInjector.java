package com.srisindhusaride.golondon;

import dagger.MembersInjector;
import javax.annotation.Generated;
import javax.inject.Provider;

@Generated(
  value = "dagger.internal.codegen.ComponentProcessor",
  comments = "https://google.github.io/dagger"
)
public final class MainActivity_MembersInjector implements MembersInjector<MainActivity> {
  private final Provider<Nearby_Fragment> nearby_fragmentProvider;

  public MainActivity_MembersInjector(Provider<Nearby_Fragment> nearby_fragmentProvider) {
    assert nearby_fragmentProvider != null;
    this.nearby_fragmentProvider = nearby_fragmentProvider;
  }

  public static MembersInjector<MainActivity> create(
      Provider<Nearby_Fragment> nearby_fragmentProvider) {
    return new MainActivity_MembersInjector(nearby_fragmentProvider);
  }

  @Override
  public void injectMembers(MainActivity instance) {
    if (instance == null) {
      throw new NullPointerException("Cannot inject members into a null reference");
    }
    instance.nearby_fragment = nearby_fragmentProvider.get();
  }

  public static void injectNearby_fragment(
      MainActivity instance, Provider<Nearby_Fragment> nearby_fragmentProvider) {
    instance.nearby_fragment = nearby_fragmentProvider.get();
  }
}
