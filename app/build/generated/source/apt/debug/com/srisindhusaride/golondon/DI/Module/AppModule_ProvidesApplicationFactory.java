package com.srisindhusaride.golondon.DI.Module;

import android.app.Application;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.annotation.Generated;

@Generated(
  value = "dagger.internal.codegen.ComponentProcessor",
  comments = "https://google.github.io/dagger"
)
public final class AppModule_ProvidesApplicationFactory implements Factory<Application> {
  private final AppModule module;

  public AppModule_ProvidesApplicationFactory(AppModule module) {
    assert module != null;
    this.module = module;
  }

  @Override
  public Application get() {
    return Preconditions.checkNotNull(
        module.providesApplication(), "Cannot return null from a non-@Nullable @Provides method");
  }

  public static Factory<Application> create(AppModule module) {
    return new AppModule_ProvidesApplicationFactory(module);
  }

  /** Proxies {@link AppModule#providesApplication()}. */
  public static Application proxyProvidesApplication(AppModule instance) {
    return instance.providesApplication();
  }
}
