package io.github.zowpy.menu;

import java.io.*;

public interface TypeCallback<T> extends Serializable
{
    void callback(final T p0);
}
