package nz.co.canadia.horseplays.script;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.OrderedMap;
import com.badlogic.gdx.utils.XmlReader;

import java.io.IOException;

import nz.co.canadia.horseplays.SpeechUI;
import nz.co.canadia.horseplays.util.Constants;

/**
 * The play script contains all the horse actor dialog and controls what happens on the Stage
 */

public class PlayScript {
    private OrderedMap<String, ScriptKnot> scriptKnots;
    private ScriptKnot currentKnot;
    private int bombThreshold;
    private int bombCount;

    public PlayScript() {

        resetBombCount();

        scriptKnots = new OrderedMap<String, ScriptKnot>();

        XmlReader xmlReader = new XmlReader();
        XmlReader.Element rootElement;
        Array<XmlReader.Element> knotElements;

        try {
            // get root element
            rootElement = xmlReader.parse(Gdx.files.internal("playscripts/playscript.xml"));

            // get knot elements
            knotElements = rootElement.getChildrenByName("knot");

            for (XmlReader.Element knot : knotElements) {

                // get knot id
                String id = knot.getAttribute("id");
                // get bombThreshold from "bomb" knot
                if (id.equals("bomb")) {
                    bombThreshold = knot.getIntAttribute("threshold");
                }
                // get knot divert
                String divert = knot.getAttribute("divert", Constants.END_KNOT);

                // get line elements and create array of ScriptLines
                Array<XmlReader.Element> lineElements = knot.getChildrenByName("line");
                Array<ScriptLine> scriptLines = new Array<ScriptLine>();
                for (XmlReader.Element line : lineElements) {
                    scriptLines.add(new ScriptLine(line.getAttribute("actor"), line.getText()));
                }

                // get scriptChoices and create array of ScriptChoices
                Array<ScriptChoice> scriptChoices = new Array<ScriptChoice>();
                XmlReader.Element choicesElement = knot.getChildByName("choices");
                if (choicesElement != null) {
                    String choiceActor = choicesElement.getAttribute("actor");

                    Array<XmlReader.Element> choiceElements = choicesElement.getChildrenByName("choice");
                    for (XmlReader.Element choice : choiceElements) {
                        scriptChoices.add(new ScriptChoice(choiceActor, choice.getText(),
                                choice.getAttribute("divert", divert),
                                choice.getIntAttribute("bomb", 0)));
                    }
                }

                // create scriptKnot and add to array
                scriptKnots.put(id, new ScriptKnot(scriptLines, scriptChoices, id, divert));

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        currentKnot = scriptKnots.get("start");

    }

    public ScriptKnot getCurrentKnot() {
        return currentKnot;
    }

    public ScriptLine getCurrentLine() {
        return currentKnot.getCurrentScriptLine();
    }

    private void nextLine() {
        currentKnot.nextLine();
    }

    public void speakCurrentLine(SpeechUI speechUI) {
        speechUI.speak(this.getCurrentLine());
        nextLine();
    }

    public boolean hasLine() {
        return currentKnot.hasLine();
    }

    public boolean hasChoice() {
        return currentKnot.hasChoice();
    }

    public void speakChoice(SpeechUI speechUI) {
        speechUI.speak(currentKnot.getChoices(), this);
    }

    public void nextKnot() {
        String divert = currentKnot.getDivert();
        currentKnot = scriptKnots.get(divert);
    }

    public void setCurrentKnot(String knot) {
        this.currentKnot = scriptKnots.get(knot);
    }

    private int getBombCount() {
        return bombCount;
    }

    private int getBombThreshold() {
        return bombThreshold;
    }

    public void addBomb(int bomb) {
        this.bombCount += bomb;
    }

    public void end(SpeechUI speechUI) {
        speechUI.end();
    }

    private void resetBombCount() {
        this.bombCount = 0;
    }

    public void checkBombCount(SpeechUI speechUI) {
        if (getBombCount() >= getBombThreshold()) {
            resetBombCount();
            setCurrentKnot("bomb");
            speakCurrentLine(speechUI);
        }
    }
}
