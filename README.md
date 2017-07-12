## UX Design Objectives

Sara.ai’s primary objective is to make reading and writing easy for the emergent reader. Some of the tools I implement may seem inefficient to a literate user, like the Dynamic Keyboard. However, my goal isn’t to make the best tools for lifelong use. Just as I don’t expect every reader to forever rely on the scaffold of Enhanced Choral Reading as their sole scaffold for word recognition, I expect users to move on to better tools as they progress in fluency. These tools I build are important because, for emergent readers, they are easier use than the best tools are.

## Word Deconstruction

If I ask someone who doesn’t speak Korean to sound out 태권도 they will have a difficult time. If I ask to simply identify the ‘letters’ it would probably still be rather difficult. Once I point them out, it perhaps gets a little easier:

### Step 1

ㅌ ㅐ ㄱ ㅜ ㅓ ㄴ ㄷ ㅗ

Next I can walk you through which sound each letter makes:

### Step 2

ㅌ says tʰ as in tall

ㅐ says ɛ as in bella, but sometimes ɛː as in rays

ㄱ says k͈ as in skin

ㅜ says w as in wall, but sometimes it’s written ㅗ, and can sometime be pronounced we

ㅓ says ʌ as in mud, but sometimes əː as in pearl

ㄴ says n as in not

ㄷ says d as in doll

ㅗ says o as in core

Now let’s put it together:

### Step 3

ㅌ – ㅐ – ㄱ – ㅜ – ㅓ – ㄴ – ㄷ – ㅗ

All that work, and we still don’t even know what the word means.

If, on the other hand, I were to tell you that 태권도 is Taekwondo you would immediately bring in meaning. Then if I asked you to point out the characters ㅌ ㅐ ㄱ ㅜ ㅓ ㄴ ㄷ ㅗ inside 태권도 you are now deconstructing the word. You could come to recognize that 태권도 is Taekwondo without going through steps 2 and 3. 

Without deconstruction 태권도 is simply strange and nebulous black doodles on a white page.

The Fluency Activity is a user interface that prompts for deconstruction, accelerating recognition.

## Dynamic Keyboard

The dynamic keyboard is designed to prevent choice overload typically associated with Hunt and Peck (two-fingered typing). The goal is to allow the user to quickly type out any unknown word for help retrieving the pronunciation, and perhaps definition. Only four letters are presented at a time. Which four to present is a simple problem of statistics.

Roughly one in three of all words in print start with these four letters: s – t – c – a. This is not correlated to the number of entries in the dictionary, but actual frequency of occurrence. Just look at the initial letters of the preceding sentence: t i n c t t n o e i t d b a f o o has 4t + 1c + 1a, or 6/17

After the initial letter is typed the number of options (max, average, and median) drops quickly. The number of options (max, average, median) for 1 letter protowords is (26, 12, 8). For 2 letter protowords it is (26, 8, 2). For example:

There are 13 options which could follow A–M. 

The most likely next states are AMO AMA AMU and AMP. 

Stepping into AMO there are only 5 options. 

The most likely next states are AMON AMOU AMOR and AMOS.

But once AMOX is typed there are only two end results: amoxicillin and amoxil

This is guided typing, to help users type out full and even complex words with ease. It’s also different than autocorrect and autocomplete, because the user is identifying each letter one by one. This prevents accidentally selecting the wrong but perhaps more common word because it looks so similar to the emergent reader. It’s also another opportunity for reinforcing deconstruction.

## Dictation

Speech to Text voice recognition is a very convenient authoring tool, but it is prone to mistakes. For a literate user it is easy to spot mistakes and to fix them. I use Word Specific Fluency modeling to highlight words that the user needs to inspect carefully. Once a word appears in the message composition window the user can also easily hear it read back. The user is also able to replace individual words, and insert before. The icon of a girl’s head (bottom right) gives instruction on how to use the interface.

## Invisible Assessment

In the Easy Reading activity the user is encouraged to turn blue highlights to green highlights to increase score. If the blue highlight is not recognized the user can open the deconstruction activity with a long touch on the highlighted word.

If a non-highlighted word is unrecognized it can be read aloud with a long touch, and then turns red.

These interactions all update the Specific Fluency Model. Though the colors are visible, they aren’t necessary for the assessment, and serve a different purpose. What I accomplish here is getting assessable behavior from the user without a quiz or asking questions; it is invisible. Users are free to simply read for leisure and to learn.

## License

This document and the associated source code (This Software) are copyright protected.

Copyright 2017, Russel Fugal

### Permissions
#### Distribution
 This Software may be distributed for non-commercial use.
#### Modification
 This Software may be modified for non-commercial use.
#### Private use
 This Software may be used and modified in private.
### Conditions
#### Disclose source
 Source code must be made available when the software is distributed.
#### License and copyright notice
 A copy of the license and copyright notice must be included with the software.
#### Same license
 Modifications must be released under the same license when distributing the software.
#### State changes
 Changes made to the code must be documented.
### Limitations
No trademark or patent rights held by Russel Fugal are waived, abandoned, surrendered, licensed or otherwise affected by this license.
#### Commercial use
 This Software and derivatives may not be used for commercial purposes without permission from Russel Fugal.
#### Liability
 This license includes a limitation of liability.

 Russel Fugal (Affirmer) disclaims responsibility for clearing rights of other persons
 that may apply to the Work or any use thereof, including without limitation
 any person's Copyright and Related Rights. Further, Affirmer
 disclaims responsibility for obtaining any necessary consents, permissions
 or other rights required for any use of the Work.
 
#### Warranty

 This Software is offered as-is and makes no representations or warranties
 of any kind, express, implied, statutory or otherwise,
 including without limitation warranties of title, merchantability, fitness
 for a particular purpose, non infringement, or the absence of latent or
 other defects, accuracy, or the present or absence of errors, whether or not
 discoverable, all to the greatest extent permissible under applicable law.
 
### Public License Fallback

Should any part of this License for any reason be judged legally invalid or ineffective under applicable law, then the Copyright and Related Rights shall be preserved to the maximum extent permitted
