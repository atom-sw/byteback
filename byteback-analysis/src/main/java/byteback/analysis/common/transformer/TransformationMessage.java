package byteback.analysis.common.transformer;

import soot.tagkit.PositionTag;
import soot.tagkit.SourceFileTag;

public class TransformationMessage extends RuntimeException {

    public enum Severity {
        INFO, WARNING, ERROR
    }

    private final Severity severity;

    private final PositionTag positionTag;

    private final SourceFileTag sourceFileTag;

    public TransformationMessage(final String message, final Severity severity,
                                 final PositionTag positionTag, final SourceFileTag sourceFileTag) {
        super(message);
        this.severity = severity;
        this.positionTag = positionTag;
        this.sourceFileTag = sourceFileTag;
    }

    public Severity getSeverity() {
        return severity;
    }

    public PositionTag getPositionTag() {
        return positionTag;
    }

    public SourceFileTag getSourceFileTag() {
        return sourceFileTag;
    }

}
