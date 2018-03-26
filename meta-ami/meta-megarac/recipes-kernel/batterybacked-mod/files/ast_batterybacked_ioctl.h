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

#ifndef _AST_BATTERY_BACKED_IOCTL_H_
#define _AST_BATTERY_BACKED_IOCTL_H_

typedef struct _BATTERYBACKED_ACCESS_DATA
{
        unsigned char Index;
        unsigned long Data;
        unsigned char Size;		
} BATTERYBACKED_ACCESS_DATA;
typedef BATTERYBACKED_ACCESS_DATA batterybacked_ioaccess_data;

#define	BATTERY_BACKED_IOCTL_KEY			'b'
#define	BATTERY_BACKED_IOCTL_READ			_IOR(BATTERY_BACKED_IOCTL_KEY, 0, batterybacked_ioaccess_data)
#define	BATTERY_BACKED_IOCTL_WRITE			_IOW(BATTERY_BACKED_IOCTL_KEY, 1, batterybacked_ioaccess_data)

#endif /* _AST_BATTERY_BACKED_IOCTL_H_ */

