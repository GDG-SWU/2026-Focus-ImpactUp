package com.gdgswu.qos.data.model

data class Phrase(
    val id: Int,
    val category: PhraseCategory,
    val english: String,        // 소스 (공통 언어)
    val french: String,         // 프랑스어
    val arabic: String,         // 아랍어
    val wolof: String,          // 월로프어 (세네갈)
    val mandinka: String,       // 만딩카어
    val fula: String            // 풀라니어
)

enum class PhraseCategory(val displayName: String, val emoji: String) {
    MEDICAL("Medical", "🏥"),
    SHELTER("Shelter", "🏠"),
    FOOD("Food & Water", "🍞"),
    EMERGENCY("Emergency", "🆘")
}

enum class SupportedLanguage(val displayName: String, val code: String) {
    ENGLISH("English", "en"),
    FRENCH("Français", "fr"),
    ARABIC("العربية", "ar"),
    WOLOF("Wolof", "wo"),
    MANDINKA("Mandinka", "ma"),
    FULA("Fulfulde", "fu")
}

val samplePhrases = listOf(
    // Medical
    Phrase(1, PhraseCategory.MEDICAL,
        english = "I am sick",
        french = "Je suis malade",
        arabic = "أنا مريض",
        wolof = "Dama feebar",
        mandinka = "Ne be kasi la",
        fula = "Mi haɗii"
    ),
    Phrase(2, PhraseCategory.MEDICAL,
        english = "Please help me",
        french = "Aidez-moi s'il vous plaît",
        arabic = "ساعدني من فضلك",
        wolof = "Dafa ma soxor",
        mandinka = "I ye ne deme",
        fula = "Wallito mi"
    ),
    Phrase(3, PhraseCategory.MEDICAL,
        english = "Where is the hospital?",
        french = "Où est l'hôpital?",
        arabic = "أين المستشفى؟",
        wolof = "Fan la hôpital bi?",
        mandinka = "Hôpital be min?",
        fula = "Hôpital e hol to?"
    ),
    Phrase(4, PhraseCategory.MEDICAL,
        english = "I am allergic to penicillin",
        french = "Je suis allergique à la pénicilline",
        arabic = "أنا حساس للبنسلين",
        wolof = "Penicilline dafa ma dëkk",
        mandinka = "Ne ye penicilline faamu",
        fula = "Mi faamaaki penicilline"
    ),
    Phrase(5, PhraseCategory.MEDICAL,
        english = "I feel dizzy",
        french = "J'ai des vertiges",
        arabic = "أشعر بالدوار",
        wolof = "Dama sedd ci bopp",
        mandinka = "Ne boŋ be yere",
        fula = "Hoore am moƴƴaani"
    ),

    // Shelter
    Phrase(6, PhraseCategory.SHELTER,
        english = "I need shelter",
        french = "J'ai besoin d'un abri",
        arabic = "أحتاج مأوى",
        wolof = "Dama soxna ker",
        mandinka = "Ne be kaso ka sɔrɔ",
        fula = "Mi jogii galle"
    ),
    Phrase(7, PhraseCategory.SHELTER,
        english = "Where is the nearest camp?",
        french = "Où est le camp le plus proche?",
        arabic = "أين أقرب مخيم؟",
        wolof = "Fan la camp bi?",
        mandinka = "Camp be min?",
        fula = "Camp e hol to?"
    ),
    Phrase(8, PhraseCategory.SHELTER,
        english = "I have children with me",
        french = "J'ai des enfants avec moi",
        arabic = "معي أطفال",
        wolof = "Am na doom yi",
        mandinka = "Ne ye denw sɔrɔ",
        fula = "Mi am ɓiɓɓe"
    ),
    Phrase(9, PhraseCategory.SHELTER,
        english = "There is a pregnant woman",
        french = "Il y a une femme enceinte",
        arabic = "هناك امرأة حامل",
        wolof = "Am na jigéen bu ndaw",
        mandinka = "Muso cɛ be dɔ",
        fula = "Debbo ɓe haɓɓaani"
    ),

    // Food & Water
    Phrase(10, PhraseCategory.FOOD,
        english = "I need water",
        french = "J'ai besoin d'eau",
        arabic = "أحتاج ماء",
        wolof = "Dama soxna ndox",
        mandinka = "Ne be ji sɔrɔ",
        fula = "Mi jogii ndiyam"
    ),
    Phrase(11, PhraseCategory.FOOD,
        english = "I am hungry",
        french = "J'ai faim",
        arabic = "أنا جائع",
        wolof = "Dama xiif",
        mandinka = "Ne be kɔngɔ la",
        fula = "Mi haɗii keeɓo"
    ),
    Phrase(12, PhraseCategory.FOOD,
        english = "Where is clean water?",
        french = "Où est l'eau potable?",
        arabic = "أين الماء النظيف؟",
        wolof = "Fan la ndox bu set bi?",
        mandinka = "Ji saniya be min?",
        fula = "Ndiyam ndaneejam e hol to?"
    ),

    // Emergency
    Phrase(13, PhraseCategory.EMERGENCY,
        english = "This is an emergency",
        french = "C'est une urgence",
        arabic = "هذه حالة طوارئ",
        wolof = "Dafa dëkk lool",
        mandinka = "Nin ye kɛnɛya dɔ ye",
        fula = "Jooɗal ngal"
    ),
    Phrase(14, PhraseCategory.EMERGENCY,
        english = "Please call for help",
        french = "Appelez les secours s'il vous plaît",
        arabic = "اتصل بالنجدة من فضلك",
        wolof = "Woo buur",
        mandinka = "I ye ladɔn",
        fula = "Noddu wallitooɓe"
    ),
    Phrase(15, PhraseCategory.EMERGENCY,
        english = "I am a refugee",
        french = "Je suis un réfugié",
        arabic = "أنا لاجئ",
        wolof = "Maa ngi réfugié",
        mandinka = "Ne ye réfugié ye",
        fula = "Mi réfugié"
    ),
    Phrase(16, PhraseCategory.EMERGENCY,
        english = "I need an interpreter",
        french = "J'ai besoin d'un interprète",
        arabic = "أحتاج مترجمًا",
        wolof = "Dama soxna interpretu",
        mandinka = "Ne be kalan maa sɔrɔ",
        fula = "Mi jogii terjoowo"
    ),
)

fun Phrase.getTranslation(language: SupportedLanguage): String = when (language) {
    SupportedLanguage.ENGLISH -> english
    SupportedLanguage.FRENCH -> french
    SupportedLanguage.ARABIC -> arabic
    SupportedLanguage.WOLOF -> wolof
    SupportedLanguage.MANDINKA -> mandinka
    SupportedLanguage.FULA -> fula
}
