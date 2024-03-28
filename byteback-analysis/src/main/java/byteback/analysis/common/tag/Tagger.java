package byteback.analysis.common.tag;

import soot.tagkit.Host;
import soot.tagkit.Tag;

public abstract class Tagger<K extends Host, V extends Tag> {

    abstract String getTagName();



}
