package it.stefan

import com.intellij.ide.BrowserUtil
import com.intellij.openapi.actionSystem.AnActionEvent

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Caret
import com.intellij.openapi.editor.Document


class SearchOnBabelNet : AnAction() {
    /**
     * Maximum len of a search term
     */
    val MAX_WORD_LEN = 100

    /**
     * BabelNet url for word query
     */
    val BABELNET_SEARCH_URL: String = "https://babelnet.org/search?word=%s&lang=EN"

    /**
     * BabelNet url for synset query
     */
    val BABELNET_SYNSET_URL: String = "https://babelnet.org/synset?id=%s&lang=EN"

    /**
     * BabelNet id prefix
     */
    val BN_PREFIX = "bn:"

    /**
     * BabelNet ID pattern
     */
    val BN_PATTERN = "^($BN_PREFIX)?[0-9]{8}[anrv]$".toRegex()




    override fun actionPerformed(e: AnActionEvent) {
        var selectedWord = extractSelectedText(e)

        if (selectedWord.length > MAX_WORD_LEN) {
            return
        }

        var searchURL: String = BABELNET_SEARCH_URL

        // Check if it is a synset
        if (BN_PATTERN.matches(selectedWord)) {
            selectedWord = getSynset(selectedWord)
            searchURL = BABELNET_SYNSET_URL
        }

        BrowserUtil.browse(searchURL.format(selectedWord))
    }

    private fun getSynset(s: String): String {
        if (!s.startsWith(BN_PREFIX))
            return BN_PREFIX + s
        return s
    }

    /**
     * Extract user selected text
     */
    private fun extractSelectedText(e: AnActionEvent): String {
        // Get all the required data from data keys
        val editor = e.getRequiredData(CommonDataKeys.EDITOR)
        val document: Document = editor.document

        // Work off of the primary caret to get the selection info
        val primaryCaret: Caret = editor.caretModel.primaryCaret
        val start: Int = primaryCaret.selectionStart
        val end: Int = primaryCaret.selectionEnd

        return document.charsSequence.substring(start, end)
    }
}