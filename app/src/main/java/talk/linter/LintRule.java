package talk.linter;

import java.util.List;

/**
 * Interface for lint rules that check Talk code
 */
public interface LintRule {
    /**
     * Check the code for issues
     * @param context The lint context containing source and tokens
     * @return List of issues found, or empty list if none
     */
    List<LintIssue> check(LintContext context);
}
