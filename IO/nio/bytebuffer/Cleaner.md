General-purpose phantom-reference-based cleaners.

Cleaners are a lightweight and more robust alternative to finalization. They are lightweight because they are not created by the VM and thus do not
require a JNI upcall to be created, and because their cleanup code is invoked directly by the reference-handler thread rather than by the
finalizer thread.  They are more robust because they use phantom references, the weakest type of reference object, thereby avoiding the nasty ordering
problems inherent to finalization.

A cleaner tracks a referent object and encapsulates a thunk of arbitrary cleanup code.  Some time after the GC detects that a cleaner's referent has
become phantom-reachable, the reference-handler thread will run the cleaner. Cleaners may also be invoked directly; they are thread safe and ensure that they run their thunks at most once.

Cleaners are not a replacement for finalization.  They should be used only when the cleanup code is extremely simple and straightforward.
Nontrivial cleaners are inadvisable since they risk blocking the reference-handler thread and delaying further cleanup and finalization.