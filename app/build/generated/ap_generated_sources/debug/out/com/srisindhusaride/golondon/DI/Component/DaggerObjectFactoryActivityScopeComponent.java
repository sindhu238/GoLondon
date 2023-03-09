package com.srisindhusaride.golondon.DI.Component;

import com.srisindhusaride.golondon.DI.Module.ObjectFactoryActivityScopeModule;
import com.srisindhusaride.golondon.MainActivity1;
import dagger.internal.DaggerGenerated;
import dagger.internal.Preconditions;
import javax.annotation.Generated;

@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes"
})
public final class DaggerObjectFactoryActivityScopeComponent {
  private DaggerObjectFactoryActivityScopeComponent() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static ObjectFactoryActivityScopeComponent create() {
    return new Builder().build();
  }

  public static final class Builder {
    private Builder() {
    }

    /**
     * @deprecated This module is declared, but an instance is not used in the component. This method is a no-op. For more, see https://dagger.dev/unused-modules.
     */
    @Deprecated
    public Builder objectFactoryActivityScopeModule(
        ObjectFactoryActivityScopeModule objectFactoryActivityScopeModule) {
      Preconditions.checkNotNull(objectFactoryActivityScopeModule);
      return this;
    }

    public ObjectFactoryActivityScopeComponent build() {
      return new ObjectFactoryActivityScopeComponentImpl();
    }
  }

  private static final class ObjectFactoryActivityScopeComponentImpl implements ObjectFactoryActivityScopeComponent {
    private final ObjectFactoryActivityScopeComponentImpl objectFactoryActivityScopeComponentImpl = this;

    private ObjectFactoryActivityScopeComponentImpl() {


    }

    @Override
    public void inject(MainActivity1 mainActivity1) {
    }
  }
}
