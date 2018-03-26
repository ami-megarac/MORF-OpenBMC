/****************************************************************
 **                                                            **
 **    (C)Copyright 2009-2015, American Megatrends Inc.        **
 **                                                            **
 **            All Rights Reserved.                            **
 **                                                            **
 **        5555 Oakbrook Pkwy Suite 200, Norcross              **
 **                                                            **
 **        Georgia - 30093, USA. Phone-(770)-246-8600.         **
 **                                                            **
****************************************************************/

#ifndef _AST_BATTERY_BACKED_H_
#define _AST_BATTERY_BACKED_H_

#define AST_BATTERYBACKED_REG_BASE	0x1E6EF000
#define AST_BATTERYBACKED_PORT_NUM	1
#define AST_BATTERYBACKED_MAX_SIZE	64

// register
#define	BATTERYBACKED_PROTECTION	0x00
#define	BATTERYBACKED_SRAM			0x100

// Unlock SRAM Timing
#define	BATTERYBACKED_FAST_TIMING	0xDBA078E0
#define	BATTERYBACKED_SLOW_TIMING	0xDBA078E2

#endif /* _AST_BATTERY_BACKED_H_ */
