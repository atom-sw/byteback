import os

import lit.formats
import lit.TestRunner


def strip_extension(name):
    return ".".join(name.split(".")[:-1])


class ByteBackShTest(lit.formats.ShTest):
    def __init__(
        self,
        src_base,
        suffixes,
        execute_external=False,
        extra_substitutions=None,
        preamble_commands=None,
    ):
        if extra_substitutions is None:
            extra_substitutions = []
        if preamble_commands is None:
            preamble_commands = []

        super().__init__(
            execute_external=execute_external,
            extra_substitutions=extra_substitutions,
            preamble_commands=preamble_commands,
        )
        self.src_base = src_base
        self.suffixes = tuple(suffixes)

    def _compute_class_name(self, test):
        source_path = test.getSourcePath()
        if not any(source_path.endswith(suffix) for suffix in self.suffixes):
            return None

        if self.src_base:
            relative_path = os.path.relpath(source_path, self.src_base)
            if relative_path.startswith(os.pardir + os.sep) or relative_path == os.pardir:
                return None
        else:
            relative_path = os.path.basename(source_path)

        return ".".join(strip_extension(relative_path).split(os.sep))

    def execute(self, test, litConfig):
        class_name = self._compute_class_name(test)
        extra_substitutions = list(self.extra_substitutions)
        if class_name:
            extra_substitutions.append(("%{class}", class_name))

        return lit.TestRunner.executeShTest(
            test,
            litConfig,
            self.execute_external,
            extra_substitutions,
            self.preamble_commands,
        )
