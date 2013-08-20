package io.prismic;

public class ApiError extends RuntimeException {

  public enum Code {
    AUTHORIZATION_NEEDED, 
    INVALID_TOKEN, 
    UNEXPECTED 
  }

  final private Code code;

  public ApiError(Code code, String message) {
    super(message);
    this.code = code;
  }

  public Code getCode() {
    return code;
  }

  public String toString() {
    return ("[" + code + "] " + getMessage());
  }

}