package nz.co.canadia.horseplays.script;

import com.badlogic.gdx.utils.Array;

/**
 * A single line of dialog from a script
 */

public class ScriptKnot {
    private Array<ScriptLine> scriptLines;
    private Array<ScriptChoice> scriptChoices;
    private int currentScriptLine;
    private String id;
    private String divert;

    ScriptKnot(Array<ScriptLine> scriptLines, Array<ScriptChoice> scriptChoices, String id, String divert) {
        this.scriptLines = scriptLines;
        currentScriptLine = 0;
        this.scriptChoices = scriptChoices;
        this.id = id;
        this.divert = divert;
    }

    ScriptLine getCurrentScriptLine() {
        return scriptLines.get(currentScriptLine);
    }

    ScriptLine nextLine() {
        currentScriptLine += 1;
        return getCurrentScriptLine();
    }
}
