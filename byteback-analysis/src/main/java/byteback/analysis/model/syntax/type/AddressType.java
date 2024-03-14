package byteback.analysis.model.syntax.type;

import byteback.common.function.Lazy;

public class AddressType extends Type {

    public static int HASHCODE = 0x74F368D1;

    private static final Lazy<AddressType> instance = Lazy.from(AddressType::new);

    public AddressType v() {
        return instance.get();
    }

    private AddressType() {};

    @Override
    public int hashCode() {
        return HASHCODE;
    }

    @Override
    public String toString() {
        return "address";
    }
}
