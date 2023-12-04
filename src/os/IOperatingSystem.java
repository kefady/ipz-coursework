package os;

import data.Request;
import data.Response;
import os.process.Process;

public interface IOperatingSystem {
    boolean request(Request request);
    void response(Response response);
    int getSysTime();
}
