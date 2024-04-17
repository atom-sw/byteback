package byteback.syntax.encoder.context;

import byteback.syntax.context.Context;

import java.io.PrintWriter;

public interface EncoderContext extends Context {

    PrintWriter getWriter();

}
