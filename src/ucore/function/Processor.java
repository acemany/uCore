package ucore.function;

public interface Processor<T, R>{
    R get(T value);
}
