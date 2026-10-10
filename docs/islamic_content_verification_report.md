# Hajj and Umrah content review

## Scope and limits

This is a source-based editorial review of the bundled React Native and Android JSON guidance, the content transformation code, and the summary documentation. It is not a fatwa, a complete review of every screen/platform, or approval by a qualified scholar. The app must not claim scholarly approval unless a named reviewer has actually reviewed and approved a dated release. Islamic rulings can differ by school and circumstance; users should ask a qualified scholar for personal cases.

Sources checked include the [Saudi Ministry of Hajj and Umrah Umrah Guide](https://haj.gov.sa/-/media/Project/HAJJ/PDF-and-FILES/umrah-hero-guide/EN-Umrah-Guide.pdf), its [Ihram Guide](https://haj.gov.sa/-/media/Project/HAJJ/Awareness-Guides/Ihram-Guide/English/EN-Ihram-Guide.pdf) and [Miqat guidance](https://haj.gov.sa/en/Umrah/Miqaats), [Qur'an 3:97](https://legacy.quran.com/3/97), [Sahih Muslim 1218a](https://sunnah.com/muslim/15/159), [Sahih Muslim 1177b](https://sunnah.com/muslim%3A1177b), [Sahih al-Bukhari 1838](https://sunnah.com/bukhari/28/18), and the [Sahih Muslim marriage chapters](https://sunnah.com/muslim/16).

## Findings and changes

- Hajj is obligatory once for a person who is able; Qur'an 3:97 establishes the obligation for those able. The app's prior Bukhari 1513 citation did not support the “once in a lifetime” wording (that report concerns a woman's Hajj on behalf of her father). The data now cites the relevant question-and-answer report as Sahih Muslim 1337.
- Umrah's core actions are entering ihram at the appropriate miqat, tawaf, sa'i between Safa and Marwa, and shaving or shortening the hair. This sequence is reflected in the Ministry guide and the Prophet's Hajj description in Muslim 1218a. The app should distinguish Hajj-specific rites from Umrah rather than imply they are identical.
- Ihram is a sacred state entered through intention, not simply the garments. A pilgrim should not pass their applicable miqat without entering ihram; entering ihram earlier is possible. The prior Android claim that ihram cannot begin before the miqat was too absolute.
- Men's ihram restrictions concern specified tailored/worn garments, not every object containing stitches. The prior “stitched clothes” phrasing was misleading. White clothing is customary/recommended, not a validity condition. Idtiba (exposing the right shoulder) applies to a particular tawaf practice, not throughout ihram.
- Women's ihram does not prescribe a special color or uniform. Face covering and gloves involve textual evidence and juristic detail; avoid presenting a one-line universal ruling without qualification. Perfume and other ihram restrictions should be described with timing and scope, not as unrestricted ordinary rules.
- The prohibition on contracting marriage while in ihram is supported by Muslim reports, but should not be conflated with all questions about engagement or a marriage arranged by another person. The data was narrowed to avoid the overly broad claim.
- Several displayed English/Arabic translations were hard-coded generic statements unrelated to the Albanian source (for example every travel rule became “intention,” and every miqat became Dhul Hulaifah). The transformation now preserves source text when no verified translation exists and maps the five miqat names individually. This intentionally favors an honest source-language fallback over invented translations.
- The former report asserted complete authentication, four-school conformity, and approval by an unnamed placeholder reviewer; it also gave incorrect or unsupported citations and described inaccurate rules as verified. Those claims have been removed. The README and settings screen must likewise avoid implying scholar verification.

## Known limitations and follow-up

The five miqat names and broad route associations are shown as a practical overview; route alignment and modern travel distances depend on the route and should not be treated as exact ritual boundaries. The bundled data's distance figures need independent geographic verification before they are presented as authoritative. Some translated content currently falls back to Albanian text, clearly preferable to a false translation but still a localization gap.

The repository contains React Native, Android, and an incomplete Flutter scaffold. This review did not establish religious accuracy for every duplicated document, generated build artifact, or platform screen. Before publishing, have the exact release content reviewed by a named qualified scholar, capture the review date and scope, and add source citations next to individual rulings. Do not label the content “100% authenticated,” “scholar-approved,” or a fatwa in the meantime.
