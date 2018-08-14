/*
 * Copyright (c) 2018 Masafumi Fujimaru
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.github.jhorology.bitwig.reflect;

import java.util.ArrayList;
import java.util.List;

/**
 *  I don't like too long method names.
 */
public class AbbrevDict {
    private static final List<String[]> REPLACES;
    static {
        REPLACES = new ArrayList<>();
        REPLACES.add(new String[] {"isMasterTrackContentShownOnTrackGroups", "showGroupMasterContent"});
        REPLACES.add(new String[] {"isRemoteControlsSectionVisible", "showRemoteControls"});
        REPLACES.add(new String[] {"([Cc])ursorRemoteControlsPage", "$1ontrolsPage"});
        REPLACES.add(new String[] {"hasAudioInputSelected", "audioInSelected"});
        REPLACES.add(new String[] {"hasNoteInputSelected", "noteInSelected"});
        REPLACES.add(new String[] {"hasNoteInputSelected", "noteInSelected"});
        REPLACES.add(new String[] {"^(.+)InsertionPoint", "$1"});
        REPLACES.add(new String[] {"canHoldAudioData", "canHoldAudio"});
        REPLACES.add(new String[] {"canHoldNoteData", "canHoldNote"});
        REPLACES.add(new String[] {"queuedForStop", "stopQueued"});
        REPLACES.add(new String[] {"([Aa]ct)ive", "$1"});
        REPLACES.add(new String[] {"([Tt])ransport", "$1p"});
        REPLACES.add(new String[] {"^(.+)OfBank", "$1"});
        REPLACES.add(new String[] {"EffectTrack", "FxTrack"});
        REPLACES.add(new String[] {"effectTrack", "fxTrack"});
        REPLACES.add(new String[] {"([Mm])aster", "$1st"});
        REPLACES.add(new String[] {"getItemAt", "at"});
        REPLACES.add(new String[] {"getParameter", "at"});
        REPLACES.add(new String[] {"^setIs(\\p{Upper}.+)", "$1"});
        REPLACES.add(new String[] {"^is(\\p{Upper}.+)", "$1"});
        REPLACES.add(new String[] {"^get(\\p{Upper}.+)", "$1"});
        REPLACES.add(new String[] {"^set(\\p{Upper}.+)", "$1"});
        REPLACES.add(new String[] {"([Mm]od)ulated", "$1"});
        REPLACES.add(new String[] {"([Dd]isp)layed", "$1"});
        REPLACES.add(new String[] {"([Vv]al)ue", "$1"});
        REPLACES.add(new String[] {"([Cc]lip)LauncherSlot", "$1"});
        REPLACES.add(new String[] {"([Cc]lip)Launcher", "$1"});
        REPLACES.add(new String[] {"([Vv]ol)ume", "$1"});
        REPLACES.add(new String[] {"([Aa]cc)ent", "$1"});
        REPLACES.add(new String[] {"Cross[fF]ade", "Xfade"});
        REPLACES.add(new String[] {"cross[fF]ade", "xfade"});
        REPLACES.add(new String[] {"([Cc]h)annel", "$1"});
        REPLACES.add(new String[] {"([Gg]r)oup", "$1"});
        REPLACES.add(new String[] {"([Tt]r)ack", "$1"});
        REPLACES.add(new String[] {"([Dd]ev)ice", "$1"});
        REPLACES.add(new String[] {"([Cc]ur)sor", "$1"});
        REPLACES.add(new String[] {"(.+)Bank", "$1s"});
        REPLACES.add(new String[] {"([Ff])orwards", "$1wd"});
        REPLACES.add(new String[] {"([Bb]ack)wards", "$1"});
        REPLACES.add(new String[] {"([Pp]os)ition", "$1"});
        REPLACES.add(new String[] {"([Ss])ource", "$1rc"});
        REPLACES.add(new String[] {"([Ss]el)ector", "$1"});
        REPLACES.add(new String[] {"([Aa]rr)anger", "$1"});
        REPLACES.add(new String[] {"([Aa]rr)angement", "$1"});
        REPLACES.add(new String[] {"([Cc])ontrol", "$1trl"});
        REPLACES.add(new String[] {"([Aa])mount", "$1mt"});
        REPLACES.add(new String[] {"([Cc])ount", "$1t"});
        REPLACES.add(new String[] {"([Rr]ec)ording", "$1"});
        REPLACES.add(new String[] {"([Rr]ec)ord", "$1"});
        REPLACES.add(new String[] {"([Aa]pp)lication", "$1"});
        REPLACES.add(new String[] {"([Ii]ns)ertion", "$1"});
        REPLACES.add(new String[] {"([Ii]ns)ert", "$1"});
        REPLACES.add(new String[] {"([Mm]et)ronome", "$1"});
        REPLACES.add(new String[] {"([Rr]em)ote", "$1"});
        // TODO need more words
    }
    
    public static String abbrev(String name) {
        name = replace(name);
        return name;
    }

    private static String replace(String name) {
        for(String p[] : REPLACES) {
            name = name.replaceFirst(p[0], p[1]);
        }
        if (Character.isUpperCase(name.charAt(0))) {
            name = Character.toLowerCase(name.charAt(0)) + name.substring(1);
        }
        return name;
    };

}
