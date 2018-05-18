package com.srisindhusaride.golondon.DI.Module;

import com.srisindhusaride.golondon.Utils.InternetConnectivityReceiver;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.annotation.Generated;

@Generated(
  value = "dagger.internal.codegen.ComponentProcessor",
  comments = "https://google.github.io/dagger"
)
public final class ObjectFactoryActivityScopeModule_ProvidesInternetConnectivityReceiverFactory
    implements Factory<InternetConnectivityReceiver> {
  private final ObjectFactoryActivityScopeModule module;

  public ObjectFactoryActivityScopeModule_ProvidesInternetConnectivityReceiverFactory(
      ObjectFactoryActivityScopeModule module) {
    assert module != null;
    this.module = module;
  }

  @Override
  public InternetConnectivityReceiver get() {
    return Preconditions.checkNotNull(
        module.providesInternetConnectivityReceiver(),
        "Cannot return null from a non-@Nullable @Provides method");
  }

  public static Factory<InternetConnectivityReceiver> create(
      ObjectFactoryActivityScopeModule module) {
    return new ObjectFactoryActivityScopeModule_ProvidesInternetConnectivityReceiverFactory(module);
  }

  /** Proxies {@link ObjectFactoryActivityScopeModule#providesInternetConnectivityReceiver()}. */
  public static InternetConnectivityReceiver proxyProvidesInternetConnectivityReceiver(
      ObjectFactoryActivityScopeModule instance) {
    return instance.providesInternetConnectivityReceiver();
  }
}
