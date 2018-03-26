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

 /*
 * File name: ast_batterybacked.c
 * This driver provides means of battery backed data in SRAM which is a 64 bytes memory.
 */

#include <linux/version.h>
#include <linux/kernel.h>
#include <linux/module.h>
#include <linux/pci.h>
#include <linux/fs.h>
#include <linux/delay.h>
#include <linux/cdev.h>
#include <asm/uaccess.h>
#include <asm/io.h>

#include "ast_batterybacked.h"
#include "ast_batterybacked_ioctl.h"

#define BATTERYBACKED_MAJOR				102
#define BATTERYBACKED_MINOR				0
#define BATTERYBACKED_MAX_DEVICES		255
#define BATTERYBACKED_DEV_NAME			"batterybacked"
#define AST_BATTERYBACKED_DRIVER_NAME	"ast_batterybacked"

static struct cdev *batterybacked_cdev;
static dev_t batterybacked_devno = MKDEV(BATTERYBACKED_MAJOR, BATTERYBACKED_MINOR);
volatile u8 *ast_batterybacked_v_add=0;


static void
enable_battery_clock(void)
{
	// Use Fast Timing to unlock SRAM
	iowrite32 (BATTERYBACKED_FAST_TIMING, (void * __iomem)ast_batterybacked_v_add + BATTERYBACKED_PROTECTION);
}

static void
disable_battery_clock(void)
{
	// Write other value to lock SRAM
	iowrite32 (0, (void * __iomem)ast_batterybacked_v_add + BATTERYBACKED_PROTECTION);
}

static int
battery_backed_read( unsigned char *data, unsigned char offset, unsigned char size)
{
	int i = 0;
	uint32_t reg = 0;	
	
	if( ((offset + size) > AST_BATTERYBACKED_MAX_SIZE ) || (offset % 4) || (size > 4) )
		return -EINVAL;
	
	reg = ioread32( (void * __iomem)ast_batterybacked_v_add + BATTERYBACKED_SRAM + offset );
	
	for (i = 0;i< size;i++)
		*(data + i) = (reg >> (8*i)) & 0xFF;

	return 0;
}

static int
battery_backed_write( unsigned char *data, unsigned char offset, unsigned char size)
{
	int i = 0;
	uint32_t reg = 0;
	
	if( ((offset + size) > AST_BATTERYBACKED_MAX_SIZE ) || (offset % 4) || (size > 4) )
		return -EINVAL;

	reg	= ioread32( (void * __iomem)ast_batterybacked_v_add + BATTERYBACKED_SRAM + offset );
	enable_battery_clock();
	for (i = 0;i< size;i++)
	{
		reg = reg & ~(0xFF << (8*i));
		reg = reg | (*(data+i) << (8*i));
	}
	iowrite32 (reg, (void * __iomem)(ast_batterybacked_v_add + BATTERYBACKED_SRAM + offset));
	disable_battery_clock();
	
	return 0;
}

static int batterybacked_open(struct inode *inode, struct file *file)
{
	return 0;
}

static int batterybacked_release(struct inode *inode, struct file *file)
{
	return 0;
}

#if (LINUX_VERSION_CODE > KERNEL_VERSION(2,6,28))
static long batterybacked_ioctl(struct file *file,unsigned int cmd, unsigned long arg)
#else
static int batterybacked_ioctl(struct inode *inode, struct file *file,unsigned int cmd, unsigned long arg)
#endif
{
	BATTERYBACKED_ACCESS_DATA BB_Access_Data;
	int ret = 0;
	
	memset (&BB_Access_Data, 0x00, sizeof(BATTERYBACKED_ACCESS_DATA));
	BB_Access_Data = *(BATTERYBACKED_ACCESS_DATA *)arg;

	switch (cmd)
	{
		case BATTERY_BACKED_IOCTL_READ:
			ret = battery_backed_read((unsigned char *)BB_Access_Data.Data, BB_Access_Data.Index, BB_Access_Data.Size);
			*(BATTERYBACKED_ACCESS_DATA *)arg = BB_Access_Data;
			break;
			
		case BATTERY_BACKED_IOCTL_WRITE:
			ret = battery_backed_write((unsigned char *)BB_Access_Data.Data, BB_Access_Data.Index, BB_Access_Data.Size);
			break;
			
		default:
			printk ( "Invalid Battery Backed IOCTL Command\n");
			return -EINVAL;
	}
	return ret;
}


static struct file_operations batterybacked_ops = {
	owner:		THIS_MODULE,
	read:		NULL,
	write:		NULL,
#if (LINUX_VERSION_CODE > KERNEL_VERSION(2,6,28))
	unlocked_ioctl:	batterybacked_ioctl,
#else
	ioctl:		batterybacked_ioctl,
#endif
	open:		batterybacked_open,
	release:	batterybacked_release,
};


int __init ast_batterybacked_init(void)
{
	int ret = 0;
	
	/* battery backed device initialization */ 
	if ((ret = register_chrdev_region (batterybacked_devno, BATTERYBACKED_MAX_DEVICES, BATTERYBACKED_DEV_NAME)) < 0)
	{
		printk (KERN_ERR "failed to register battery backed device <%s> (err: %d)\n", BATTERYBACKED_DEV_NAME, ret);
		return ret;
	}
   
	batterybacked_cdev = cdev_alloc ();
	if (!batterybacked_cdev)
	{
		unregister_chrdev_region (batterybacked_devno, BATTERYBACKED_MAX_DEVICES);
		printk (KERN_ERR "%s: failed to allocate battery backed cdev structure\n", BATTERYBACKED_DEV_NAME);
		return -1;
	}
   
	cdev_init (batterybacked_cdev, &batterybacked_ops);
	
	batterybacked_cdev->owner = THIS_MODULE;
	
	if ((ret = cdev_add (batterybacked_cdev, batterybacked_devno, BATTERYBACKED_MAX_DEVICES)) < 0)
	{
		cdev_del (batterybacked_cdev);
		unregister_chrdev_region (batterybacked_devno, BATTERYBACKED_MAX_DEVICES);
		printk (KERN_ERR "failed to add <%s> char device\n", BATTERYBACKED_DEV_NAME);
		ret = -ENODEV;
		return ret;
	}
	
	ast_batterybacked_v_add = ioremap(AST_BATTERYBACKED_REG_BASE, 0x1000);
	if (!ast_batterybacked_v_add) 
	{
		printk(KERN_WARNING "%s: ioremap failed\n", BATTERYBACKED_DEV_NAME);
		unregister_chrdev_region (batterybacked_devno, BATTERYBACKED_MAX_DEVICES);
		return -ENOMEM;
	}	
	
	printk("The Battery Backed Driver is loaded successfully.\n" );
	return ret;
}

void __exit ast_batterybacked_exit(void)
{
	unregister_chrdev_region (batterybacked_devno, BATTERYBACKED_MAX_DEVICES);

	if (NULL != batterybacked_cdev)
	{
		cdev_del (batterybacked_cdev);
	}
	iounmap (ast_batterybacked_v_add);
	
	return;
}

module_init (ast_batterybacked_init);
module_exit (ast_batterybacked_exit);

MODULE_AUTHOR("American Megatrends Inc.");
MODULE_DESCRIPTION("ASPEED AST SoC Battery Backed Driver");
MODULE_LICENSE ("GPL");
