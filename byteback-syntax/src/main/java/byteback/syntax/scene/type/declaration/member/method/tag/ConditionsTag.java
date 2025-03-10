package byteback.syntax.scene.type.declaration.member.method.tag;

import soot.Value;
import soot.ValueBox;
import soot.grimp.internal.ExprBox;
import soot.tagkit.Tag;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author paganma
 */
public abstract class ConditionsTag implements Tag {

	private final List<ValueBox> conditionBoxes;

	public ConditionsTag(final List<Value> conditions) {
		this.conditionBoxes = conditions.stream()
				.map((value) -> (ValueBox) new ExprBox(value))
				.collect(Collectors.toList());
	}

	public List<ValueBox> getConditionBoxes() {
		return conditionBoxes;
	}

	public List<Value> getConditions() {
		return conditionBoxes.stream().map(ValueBox::getValue).toList();
	}

	public List<ValueBox> getUseBoxes() {
		final var useBoxes = new ArrayList<ValueBox>();

		for (final ValueBox conditionBox : conditionBoxes) {
			useBoxes.add(conditionBox);
			useBoxes.addAll(conditionBox.getValue().getUseBoxes());
		}

		return useBoxes;
	}

	public void addConditionBox(final ValueBox conditionBox) {
		conditionBoxes.add(conditionBox);
	}

	public void addConditionBoxes(final Collection<ValueBox> conditionBoxes) {
		conditionBoxes.addAll(conditionBoxes);
	}

	public void addCondition(final Value condition) {
		addConditionBox(new ExprBox(condition));
	}

	public void addConditions(final List<Value> conditions) {
		for (final Value condition : conditions) {
			addCondition(condition);
		}
	}

	@Override
	public byte[] getValue() {
		return new byte[0];
	}

}
