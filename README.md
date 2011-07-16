Vash
====

Make Data Memorable


About
-----

Vash is a family of visual hashing algorithms that take data and convert it into images.  As with a hash function, the same input will always produce the same output and similar inputs will reliably produce significantly different outputs.  Generally you can use Vash anywhere you would normally want to show a hash function.  Unlike a hash function however, you can show the output to the user.  This opens up many exciting new possibilities, some of which we discuss in the "Example Applications" section.

More information, sample output, and complete documentation may be found at: http://www.thevash.com


License and Support
-------------------
Vash is available under the AGPLv3 license. 

Additional licensing options, as well as full commercial support and custom development, are available at http://www.thevash.com


Installation
------------

Vash is a stand-alone Java Virtual Machine (JVM) jar file.  It can be run with the JVM on the command line, used directly from within JVM application code, and embedded in a web-page as an applet.  There is no installation process: simply put Vash.jar somewhere convenient for your application.


Getting the Source
------------------

Visit us on GitHub at https://github.com/thevash/vash for full source downloads or to clone the repository.


Building
--------

Import the .project file in the base directory into Eclipse.  The built source should appear after a few seconds in bin.  Use the Makefile to create docs, jars, and distribution packages.


Running Vash on the command line
-------
Vash can be run from the command-line with:
> `java -jar Vash.jar [options]`.

* **--help**
	
	Show basic usage information.

* **-V/--version**

	Print Vash's version string.

* **--known-algorithms**

	Print a list of all algorithm strings that are valid to pass to -a/--algorithm.

###Required Options:

* **-a/--algorithm <algorithm id>**

_What is the algorithm id?:_
	Although Vash only has one core mechanism, it is possible to tweak the parameters of this mechanism to achieve wildly different results.  When, at some point in the future, we want to extend Vash with ever more variety, we will need to tweak these parameters.  If we did so universally, it would not be possible to upgrade, ever.  Thus, when you want to hash some data, you must specify the specific algorithm parameters you want.  The algorithm id is a string; at the moment there are only two algorithms: "1" and "1-fast".

_How do I use it?:_
	The algorithm is a required parameter because we want you to be able to upgrade to new algorithms as they are released.  If you do not store this id with your data, then you will have to assume a single algorithm, application wide.  Instead, you should think of this value like a salt.  Store it with the data you are hashing and when you add new data to the system, you can use any algorithm you want, rather than being constrained to always use the algorithm that was most recent when you first integrated Vash.

_What is the difference between the algorithms?:_
	"1-fast" is slightly faster than "1" on large data sets, but is significantly less secure:  it uses md5 internally instead of sha-512.  Only use "1-fast" if you have specific performance problems with "1".


* **-d/--data <string>**

	This is simply the data you are going to hash.


###Optional Arguments:

* **-o/--output <string>**

	By default Vash will write its output to "output.png" in the current directory, overwriting anything by that name already there.  Use this option to specify a different output file.  Supported extensions are "png", "jpg", and "bmp".  Vash will automatically select the correct format, based on the extension.

* **-w/--width <int>**
* **-h/--height <int>**

	The default image size is 128x128.  Vash images are always logically square.  If you double the width, you will get the same square image, but stretched out.  If the distortion is small, this is probably fine for most applications.  If you want to get non-square images that preserve Vash's look across a larger distortion, we suggest that you generate a square image with the larger dimension and then crop to scale, e.g. with ImageMagick's command line tools.
	
_For example:_

* java -jar Vash.jar -a 1 -d "Foo" -w 1920 -h 1920 -o desktop.png
* convert desktop.png -crop 1920x1080+0+420 +repage desktop.png


Example Applications
--------------------

A few obvious example applications of Vash are included below. This is by no means an exhaustive list, and we expect to see Vash used in so many smart and innovative ways that these look quaint and silly by comparison. If you've got a great idea for Vash, or even better, have put Vash to an interesting use, drop us a note at info@thevash.com and we'll help you tell the world about it.


_Public Key Fingerprints:_	

When using public key cryptography, you need to trade public keys when initiating a connection.  Unfortunately, this opens up the possibility of a man-in-the-middle attack, since the eavesdropper can simply forward her own public key to each party and proxy the connection.  This attack is only defeated if one side or the other checks that the public key they are connecting to is correct.  At the moment, this generally involves checking that two long hex strings (the key fingerprints) match.  With Vash, you can present your end users with a fingerprint that they will actually be able to recognize, increasing the security of your application.
	
OpenSSH already does something very much like this, showing an ASCII-art representation of the fingerprint, in addition to the raw hex string.


_Reliable password entry:_

Currently, when a user enters a password, all they get are dots: there is no way to know if there is a typo in the entry.  With Vash, you can hash and show the password field without revealing the contents.  Your users will be able to see when they have entered their password correctly, because Vash will show them the same image they saw when they entered their password at sign-up and when they have logged on before.

	
_Verification of Sensitive Information:_

Normally, merchants and banks write credit card numbers as ************1234 when they want to hide the details but allow you to verify that it is your unique card.  This still reveals some information about the card, however, and given the non-random generation function for CC numbers there are potential collisions and security concerns.  Instead, card issuers could print the Vash image for the credit card number on the front of the card; then merchants and banks can verify the card number with the card-holder by presenting the Vash of the card number.  This method would not reveal any card information, and would be less susceptible to collisions.


_Default Forum or Blog Avatars:_

Instead of showing generic silhouettes or simple geometric shapes, Vash can create striking and distinctive artwork for use as default forum or blog comment avatars. In addition to bringing some beauty and distinction to each avatar, Vash generates a significantly wider range of abstract artwork than other generators, which lets your users retain a more distinctive and memorable identity, automatically and by default.

