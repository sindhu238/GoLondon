package com.srisindhusaride.golondon.DI.Module;

import com.srisindhusaride.golondon.Utils.InternetConnectivityReceiver;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.annotation.Generated;

@Generated(
  value = "dagger.internal.codegen.ComponentProcessor",
  comments = "https://google.github.io/dagger"
)
public final class CheckInternetModule_ProvidesInternetConnectivityReceiverFactory
    implements Factory<InternetConnectivityReceiver> {
  private final CheckInternetModule module;

  public CheckInternetModule_ProvidesInternetConnectivityReceiverFactory(
      CheckInternetModule module) {
    assert module != null;
    this.module = module;
  }

  @Override
  public InternetConnectivityReceiver get() {
    return Preconditions.checkNotNull(
        module.providesInternetConnectivityReceiver(),
        "Cannot return null from a non-@Nullable @Provides method");
  }

  public static Factory<InternetConnectivityReceiver> create(CheckInternetModule module) {
    return new CheckInternetModule_ProvidesInternetConnectivityReceiverFactory(module);
  }
}
